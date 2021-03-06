#import <MAMapKit/MAMapView.h>
#import <MAMapKit/MAGeometry.h>
#import <React/RCTConvert.h>
#import <React/RCTConvert+CoreLocation.h>
#import "Coordinate.h"

@implementation RCTConvert (AMapView)

RCT_ENUM_CONVERTER(MAMapType, (@{
        @"standard": @(MAMapTypeStandard),
        @"satellite": @(MAMapTypeSatellite),
        @"navigation": @(MAMapTypeNavi),
        @"night": @(MAMapTypeStandardNight),
        @"bus": @(MAMapTypeBus),
}), MAMapTypeStandard, integerValue)

RCT_ENUM_CONVERTER(MAPinAnnotationColor, (@{
        @"red": @(MAPinAnnotationColorRed),
        @"green": @(MAPinAnnotationColorGreen),
        @"purple": @(MAPinAnnotationColorPurple),
}), MAPinAnnotationColorRed, integerValue)

+ (Coordinate *)Coordinate:(id)json {
    return [[Coordinate alloc] initWithCoordinate: [self CLLocationCoordinate2D:json]];
}

+ (MACoordinateSpan)MACoordinateSpan:(id)json {
    json = [self NSDictionary:json];
    return (MACoordinateSpan){
        [self CLLocationDegrees:json[@"latitudeDelta"]],
        [self CLLocationDegrees:json[@"longitudeDelta"]]
    };
}

+ (MACoordinateRegion)MACoordinateRegion:(id)json {
    return (MACoordinateRegion){
        [self CLLocationCoordinate2D:json[@"center"]],
        [self MACoordinateSpan:json[@"span"]]
    };
}

+ (MAMapStatus *)MAMapStatus:(id)json {
    return [MAMapStatus statusWithCenterCoordinate:(json[@"coordinate"] ? [self CLLocationCoordinate2D:json[@"coordinate"]] : (CLLocationCoordinate2D){-1.0f, -1.0f})
                                    zoomLevel:json[@"zoomLevel"] ? [self CGFloat:json[@"zoomLevel"]] : -1
                                    rotationDegree:json[@"rotate"] ? [self CGFloat:json[@"rotate"]] : -1
                                    cameraDegree:json[@"tilt"] ? [self CGFloat:json[@"tilt"]] : -1
                                    screenAnchor:json[@"anchor"] ? [self CGPoint:json[@"anchor"]] : CGPointMake(-1, -1)];
}

RCT_ARRAY_CONVERTER(Coordinate)

@end
