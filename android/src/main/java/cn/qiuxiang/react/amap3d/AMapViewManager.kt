package cn.qiuxiang.react.amap3d

import android.util.Log
import android.view.View
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdate
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Suppress("unused")
internal class AMapViewManager : ViewGroupManager<AMapView>() {
    companion object {
        val ANIMATE_TO_COORDINATE = 1
        val ANIMATE_TO_ZOOM_LEVEL = 2
        val ANIMATE_TO_MAP_STATUS = 3
    }

    override fun getName(): String {
        return "AMapView"
    }

    override fun createViewInstance(reactContext: ThemedReactContext): AMapView {
        return AMapView(reactContext)
    }

    override fun getCommandsMap(): Map<String, Int> {
        return mapOf(
                "animateToCoordinate" to ANIMATE_TO_COORDINATE,
                "animateToZoomLevel" to ANIMATE_TO_ZOOM_LEVEL,
                "animateToMapStatus" to ANIMATE_TO_MAP_STATUS)
    }

    override fun receiveCommand(overlay: AMapView, commandId: Int, args: ReadableArray?) {
        when (commandId) {
            ANIMATE_TO_COORDINATE -> overlay.animateToCoordinate(args)
            ANIMATE_TO_ZOOM_LEVEL -> overlay.animateToZoomLevel(args)
            ANIMATE_TO_MAP_STATUS -> overlay.animateToMapStatus(args)
        }
    }

    override fun addView(mapView: AMapView, child: View, index: Int) {
        super.addView(mapView, child, index)
        when (child) {
            is AMapMarker -> mapView.addMarker(child)
            is AMapPolyline -> mapView.addPolyline(child)
            is AMapPolygon -> mapView.addPolygon(child)
            is AMapCircle -> mapView.addCircle(child)
        }
    }

    override fun removeViewAt(parent: AMapView, index: Int) {
        parent.remove(parent.getChildAt(index))
        super.removeViewAt(parent, index)
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        return mapOf(
                "onMapClick" to mapOf("registrationName" to "onPress"),
                "onMapLongClick" to mapOf("registrationName" to "onLongPress"),
                "onAnimateCancel" to mapOf("registrationName" to "onAnimateCancel"),
                "onAnimateFinish" to mapOf("registrationName" to "onAnimateFinish"),
                "onLocationChange" to mapOf("registrationName" to "onLocation"))
    }

    @ReactProp(name = "locationEnabled")
    fun setMyLocationEnabled(view: AMapView, enabled: Boolean) {
        view.map.isMyLocationEnabled = enabled
    }

    @ReactProp(name = "showsIndoorMap")
    fun showIndoorMap(view: AMapView, show: Boolean) {
        view.map.showIndoorMap(show)
    }

    @ReactProp(name = "showsIndoorSwitch")
    fun setIndoorSwitchEnabled(view: AMapView, show: Boolean) {
        view.map.uiSettings.isIndoorSwitchEnabled = show
    }

    @ReactProp(name = "showsBuildings")
    fun showBuildings(view: AMapView, show: Boolean) {
        view.map.showBuildings(show)
    }

    @ReactProp(name = "showsLabels")
    fun showMapText(view: AMapView, show: Boolean) {
        view.map.showMapText(show)
    }

    @ReactProp(name = "showsCompass")
    fun setCompassEnabled(view: AMapView, show: Boolean) {
        view.map.uiSettings.isCompassEnabled = show
    }

    @ReactProp(name = "showsZoomControls")
    fun setZoomControlsEnabled(view: AMapView, enabled: Boolean) {
        view.map.uiSettings.isZoomControlsEnabled = enabled
    }

    @ReactProp(name = "showsScale")
    fun setScaleControlsEnabled(view: AMapView, enabled: Boolean) {
        view.map.uiSettings.isScaleControlsEnabled = enabled
    }

    @ReactProp(name = "showsLocationButton")
    fun setMyLocationButtonEnabled(view: AMapView, enabled: Boolean) {
        view.map.uiSettings.isMyLocationButtonEnabled = enabled
    }

    @ReactProp(name = "showsTraffic")
    fun setTrafficEnabled(view: AMapView, enabled: Boolean) {
        view.map.isTrafficEnabled = enabled
    }

    @ReactProp(name = "maxZoomLevel")
    fun setMaxZoomLevel(view: AMapView, zoomLevel: Float) {
        view.map.maxZoomLevel = zoomLevel
    }

    @ReactProp(name = "minZoomLevel")
    fun setMinZoomLevel(view: AMapView, zoomLevel: Float) {
        view.map.minZoomLevel = zoomLevel
    }

    @ReactProp(name = "zoomLevel")
    fun setZoomLevel(view: AMapView, zoomLevel: Float) {
        view.map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
    }

    @ReactProp(name = "mapType")
    fun setMapType(view: AMapView, mapType: String) {
        when (mapType) {
            "standard" -> view.map.mapType = AMap.MAP_TYPE_NORMAL
            "satellite" -> view.map.mapType = AMap.MAP_TYPE_SATELLITE
            "navigation" -> view.map.mapType = AMap.MAP_TYPE_NAVI
            "night" -> view.map.mapType = AMap.MAP_TYPE_NIGHT
            "bus" -> view.map.mapType = AMap.MAP_TYPE_BUS
        }
    }

    @ReactProp(name = "customMapStyleName")
    fun setCustomMapStyleName(view: AMapView, name: String) {
        view.map.setMapCustomEnable(true)
        var file = File(view.context.cacheDir, name)
        if (!file.exists()) {
            try {
                var fi = view.resources.assets.open(name)
                var fo = FileOutputStream(file)
                fi.copyTo(fo)

                fi.close()
                fo.close()
            } catch (e: Exception) {
            }
        }

        view.map.setCustomMapStylePath(file.absolutePath)
    }

    @ReactProp(name = "zoomEnabled")
    fun setZoomGesturesEnabled(view: AMapView, enabled: Boolean) {
        view.map.uiSettings.isZoomGesturesEnabled = enabled
    }

    @ReactProp(name = "scrollEnabled")
    fun setScrollGesturesEnabled(view: AMapView, enabled: Boolean) {
        view.map.uiSettings.isScrollGesturesEnabled = enabled
    }

    @ReactProp(name = "rotateEnabled")
    fun setRotateGesturesEnabled(view: AMapView, enabled: Boolean) {
        view.map.uiSettings.isRotateGesturesEnabled = enabled
    }

    @ReactProp(name = "tiltEnabled")
    fun setTiltGesturesEnabled(view: AMapView, enabled: Boolean) {
        view.map.uiSettings.isTiltGesturesEnabled = enabled
    }

    @ReactProp(name = "coordinate")
    fun moveToCoordinate(view: AMapView, coordinate: ReadableMap?) {
        if (coordinate != null) {
            view.map.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(
                    coordinate.getDouble("latitude"),
                    coordinate.getDouble("longitude"))))
        }
    }

    @ReactProp(name = "region")
    fun moveToRegion(view: AMapView, region: ReadableMap?) {
        if (region != null) {
            view.map.moveCamera(CameraUpdateFactory.newLatLngBounds(AMapConverter.LatLngBounds(region), 50))
        }
    }

    @ReactProp(name = "limitRegion")
    fun setLimitRegion(view: AMapView, limitRegion: ReadableMap?) {
        if (limitRegion != null) {
            view.map.setMapStatusLimits(AMapConverter.LatLngBounds(limitRegion))
        }
    }

    @ReactProp(name = "tilt")
    fun changeTilt(view: AMapView, tilt: Float) {
        view.map.moveCamera(CameraUpdateFactory.changeTilt(tilt))
    }

    @ReactProp(name = "rotate")
    fun changeRotate(view: AMapView, rotate: Float) {
        view.map.moveCamera(CameraUpdateFactory.changeBearing(rotate))
    }

    // todo: 自定义 locationStyle
}