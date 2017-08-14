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
    setTimeout(() => {
      if (this == null) {
        return
      }
      UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            UIManager.AMapOverlay.Commands.update,
            null,
          )
    }, 0)
  }

  render() {
    return <AMapOverlay {...this.props}/>
  }
}

AMapOverlay = requireNativeComponent('AMapOverlay', Overlay)

export default Overlay
