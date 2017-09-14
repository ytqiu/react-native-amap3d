#import <React/UIView+React.h>
#import "AMapView.h"
#import "AMapMarker.h"
#import "AMapPolyline.h"

#pragma ide diagnostic ignored "OCUnusedMethodInspection"

@implementation AMapView {
}

- (void)setCustomMapStyleName:(NSString *)name {
    NSURL *msu = [[NSBundle mainBundle] URLForResource:name withExtension:nil];
    if (msu) {
        [super setCustomMapStyleEnabled:YES];
        [super setCustomMapStyleWithWebData:[NSData dataWithContentsOfURL:msu]];
    }
}

- (void)setShowsTraffic:(BOOL)shows {
    super.showTraffic = shows;
}

- (void)setTiltEnabled:(BOOL)enabled {
    super.rotateCameraEnabled = enabled;
}

- (void)setLocationEnabled:(BOOL)enabled {
    super.showsUserLocation = enabled;
}

- (void)setCoordinate:(CLLocationCoordinate2D)json {
    super.centerCoordinate = json;
}

- (void)setRegion:(MACoordinateRegion)region {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1f * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [super setRegion:MACoordinateRegionMake(region.center, MACoordinateSpanMake(region.span.latitudeDelta * 2, region.span.longitudeDelta * 2)) animated:NO];
    });
}

- (void)setTilt:(CGFloat)degree {
    super.cameraDegree = degree;
}

- (void)setRotate:(CGFloat)rotate {
    super.rotationDegree = rotate;
}

- (void)insertReactSubview:(id <RCTComponent>)subview atIndex:(NSInteger)atIndex {
    [super insertReactSubview:(UIView *) subview atIndex:atIndex];
    if ([subview isKindOfClass:[AMapMarker class]]) {
        ((AMapMarker *) subview).mapView = self;
        [self addAnnotation:(id <MAAnnotation>) subview];
    }
    if ([subview isKindOfClass:[AMapModel class]]) {
        [self addOverlay:(id <MAOverlay>) subview];
    }
}

- (void)removeReactSubview:(id <RCTComponent>)subview {
    [super removeReactSubview:(UIView *) subview];
    if ([subview isKindOfClass:[AMapMarker class]]) {
        [self removeAnnotation:(id <MAAnnotation>) subview];
    }
    if ([subview isKindOfClass:[AMapModel class]]) {
        [self removeOverlay:(id <MAOverlay>) subview];
    }
}

- (void)didUpdateReactSubviews {
}

- (void)dealloc {
    NSLog(@"amap dealloc");
}

@end
