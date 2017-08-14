package cn.qiuxiang.react.amap3d

import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.views.view.ReactViewGroup

class AMapPolyline(context: ThemedReactContext) : ReactViewGroup(context) {
    var polyline: Polyline? = null
        private set

    var width: Float = 1f
        set(value) {
            field = value
            polyline?.width = value
        }

    var color: Int = Color.BLACK
        set(value) {
            field = value
            polyline?.color = value
        }

    var opacity: Float = 1f
        set(value) {
            val tvalue = Math.max(0.01f, value)
            field = tvalue
            polyline?.setTransparency(tvalue)
        }

    var zIndex: Float = 0f
        set(value) {
            field = value
            polyline?.zIndex = value
        }

    var geodesic: Boolean = false
        set(value) {
            field = value
            polyline?.isGeodesic = value
        }

    var dashed: Boolean = false
        set(value) {
            field = value
            polyline?.isDottedLine = value
        }

    var gradient: Boolean = false

    private var coordinates: ArrayList<LatLng> = ArrayList()
    private var colors: ArrayList<Int> = ArrayList()

    fun setCoordinates(coordinates: ReadableArray) {
        this.coordinates = ArrayList((0..coordinates.size() - 1)
                .map { coordinates.getMap(it) }
                .map { LatLng(it.getDouble("latitude"), it.getDouble("longitude")) })

        polyline?.points = this.coordinates
    }

    fun setColors(colors: ReadableArray) {
        this.colors = ArrayList((0..colors.size() - 1).map { colors.getInt(it) })
    }

    fun addToMap(mapView: AMapView) {
        polyline = mapView.map.addPolyline(PolylineOptions()
                .addAll(coordinates)
                .color(AMapConverter.color(color, opacity))
                .colorValues(colorValues())
                .width(width)
                .useGradient(gradient)
                .geodesic(geodesic)
                .setDottedLine(dashed)
                .transparency(opacity)
                .zIndex(zIndex))
    }

    private fun colorValues(): List<Int> {
        if (colors.count() > 0) {
            return colors.map { colorTmp -> AMapConverter.color(colorTmp, opacity) }
        }
        return listOf(AMapConverter.color(color, opacity), AMapConverter.color(color, opacity))
    }
}
