package cn.qiuxiang.react.amap3d

import android.annotation.SuppressLint
import android.view.View
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MyLocationStyle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter

class AMapView(context: ThemedReactContext) : MapView(context) {
    private val eventEmitter: RCTEventEmitter = context.getJSModule(RCTEventEmitter::class.java)
    private val markers = HashMap<String, AMapMarker>()
    private val polylines = HashMap<String, AMapPolyline>()
    private val polygons = HashMap<String, AMapPolygon>()
    private val circles = HashMap<String, AMapCircle>()

    init {
        super.onCreate(null)

        // 设置默认的定位模式
        val locationStyle = MyLocationStyle()
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        map.myLocationStyle = locationStyle

        map.setOnMapClickListener { latLng ->
            for (marker in markers.values) {
                marker.active = false
            }

            val event = Arguments.createMap()
            event.putDouble("latitude", latLng.latitude)
            event.putDouble("longitude", latLng.longitude)
            emit(id, "onMapClick", event)
        }

        map.setOnMapLongClickListener { latLng ->
            val event = Arguments.createMap()
            event.putDouble("latitude", latLng.latitude)
            event.putDouble("longitude", latLng.longitude)
            emit(id, "onMapLongClick", event)
        }

        map.setOnMyLocationChangeListener { location ->
            val event = Arguments.createMap()
            event.putDouble("latitude", location.latitude)
            event.putDouble("longitude", location.longitude)
            event.putDouble("accuracy", location.accuracy.toDouble())
            emit(id, "onLocationChange", event)
        }

        map.setOnMarkerClickListener { marker ->
            emit(markers[marker.id]?.id, "onMarkerClick", Arguments.createMap())
            false
        }

        map.setOnMarkerDragListener(object : AMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                emit(markers[marker.id]?.id, "onMarkerDragStart")
            }

            override fun onMarkerDrag(marker: Marker) {
                emit(markers[marker.id]?.id, "onMarkerDrag")
            }

            override fun onMarkerDragEnd(marker: Marker) {
                val position = marker.position
                val data = Arguments.createMap()
                data.putDouble("latitude", position.latitude)
                data.putDouble("longitude", position.longitude)
                emit(markers[marker.id]?.id, "onMarkerDragEnd", data)
            }
        })

        map.setOnInfoWindowClickListener { marker ->
            emit(markers[marker.id]?.id, "onInfoWindowClick")
        }

        map.setOnPolylineClickListener { polyline ->
            emit(polylines[polyline.id]?.id, "onPolylineClick")
        }

        map.setInfoWindowAdapter(InfoWindowAdapter(context, markers))
    }

    @SuppressLint("MissingSuperCall")
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        map.isMyLocationEnabled = false
    }

    fun addMarker(marker: AMapMarker) {
        marker.addToMap(this)
        markers.put(marker.marker?.id!!, marker)
    }

    fun addPolyline(polyline: AMapPolyline) {
        polyline.addToMap(this)
        polylines.put(polyline.polyline?.id!!, polyline)
    }

    fun  addPolygon(polygon: AMapPolygon) {
        polygon.addToMap(this)
        polygons.put(polygon.polygon?.id!!, polygon)
    }

    fun addCircle(circle: AMapCircle) {
        circle.addToMap(this)
        circles.put(circle.circle?.id!!, circle)
    }

    fun emit(id: Int?, name: String, data: WritableMap = Arguments.createMap()) {
        id?.let { eventEmitter.receiveEvent(it, name, data) }
    }

    fun remove(child: View) {
        when (child) {
            is AMapMarker -> {
                markers.remove(child.marker?.id)
                child.marker?.destroy()
            }
            is AMapPolyline -> {
                polylines.remove(child.polyline?.id)
                child.polyline?.remove()
            }
            is AMapPolygon -> {
                polygons.remove(child.polygon?.id)
                child.polygon?.remove()
            }
            is AMapCircle -> {
                polygons.remove(child.circle?.id)
                child.circle?.remove()
            }
        }
    }

    val animateCallback = object: AMap.CancelableCallback {
        override fun onCancel() {
            emit(id, "onAnimateCancel")
        }

        override fun onFinish() {
            emit(id, "onAnimateFinish")
        }
    }

    fun animateToCoordinate(args: ReadableArray?) {
        val coordinate = args?.getMap(0)!!
        val duration = args.getInt(1)
        val cameraUpdate = CameraUpdateFactory.newLatLng(LatLng(
                coordinate.getDouble("latitude"), coordinate.getDouble("longitude")))

        if (duration <= 0) {
            map.moveCamera(cameraUpdate)
        } else {
            map.animateCamera(cameraUpdate, duration.toLong(), animateCallback)
        }
    }

    fun animateToZoomLevel(args: ReadableArray?) {
        val zoomLevel = args?.getDouble(0)!!
        val duration = args.getInt(1)
        val cameraUpdate = CameraUpdateFactory.zoomTo(zoomLevel.toFloat())

        if (duration <= 0) {
            map.moveCamera(cameraUpdate)
        } else {
            map.animateCamera(cameraUpdate, duration.toLong(), animateCallback)
        }
    }

    fun animateToMapStatus(args: ReadableArray?) {
        val mapStatusMap = args?.getMap(0)
        val duration = args?.getInt(1) ?: 0
        if (mapStatusMap == null) {
            return
        }

        val centerCoordinate = if (mapStatusMap.hasKey("coordinate")) AMapConverter.LatLng(mapStatusMap.getMap("coordinate")) else null
        val zoomLevel = if (mapStatusMap.hasKey("zoomLevel")) mapStatusMap.getDouble("zoomLevel").toFloat() else null
        val rotationDegree = if (mapStatusMap.hasKey("rotate")) mapStatusMap.getDouble("rotate").toFloat() else null
        val cameraDegree = if (mapStatusMap.hasKey("tilt")) mapStatusMap.getDouble("tilt").toFloat() else null
        val position = CameraPosition(centerCoordinate ?: map.cameraPosition.target, zoomLevel ?: map.cameraPosition.zoom, cameraDegree ?: map.cameraPosition.tilt, rotationDegree ?: map.cameraPosition.bearing)
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(position)

        if (duration <= 0) {
            map.moveCamera(cameraUpdate)
        } else {
            map.animateCamera(cameraUpdate, duration.toLong(), animateCallback)
        }
    }
}