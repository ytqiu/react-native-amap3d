package cn.qiuxiang.react.amap3d

import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Polygon
import com.amap.api.maps.model.PolygonOptions
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.views.view.ReactViewGroup

class AMapPolygon(context: ThemedReactContext) : ReactViewGroup(context) {
    var polygon: Polygon? = null
        private set

    var strokeWidth: Float = 1f
        set(value) {
            field = value
            polygon?.strokeWidth = value
        }

    var strokeColor: Int = Color.BLACK
        set(value) {
            field = value
            polygon?.strokeColor = value
        }

    var fillColor: Int = Color.BLACK
        set(value) {
            field = value
            polygon?.fillColor = value
        }

    var zIndex: Float = 0f
        set(value) {
            field = value
            polygon?.zIndex = value
        }

    private var coordinates: ArrayList<LatLng> = ArrayList()

    fun setCoordinates(coordinates: ReadableArray) {
        this.coordinates = ArrayList((0..coordinates.size() - 1)
                .map { coordinates.getMap(it) }
                .map { LatLng(it.getDouble("latitude"), it.getDouble("longitude")) })

        polygon?.points = this.coordinates
    }

    fun addToMap(mapView: AMapView) {
        polygon = mapView.map.addPolygon(PolygonOptions()
                .addAll(coordinates)
                .strokeColor(strokeColor)
                .strokeWidth(strokeWidth)
                .fillColor(fillColor)
                .zIndex(zIndex))
    }
}
