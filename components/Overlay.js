import React, {PropTypes, Component} from 'react'
import {
  View,
  UIManager,
  Platform,
  NativeModules,
  findNodeHandle,
  requireNativeComponent,
} from 'react-native'

class Overlay extends Component {
  static propTypes = {
    ...View.propTypes,
  }
  
  componentDidUpdate() {
    this.update()
  }
 
  update() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.AMapOverlay.Commands.update,
      null,
    )
  }

  render() {
    return <AMapOverlay {...this.props}/>
  }
}

AMapOverlay = requireNativeComponent('AMapOverlay', Overlay)

export default Overlay
