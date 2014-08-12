/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.virca.api;

public class VirCAJavaApiJNI {
  public final static native long init();
  public final static native long new_CyberDeviceAPIEventHandler();
  public final static native void CyberDeviceAPIEventHandler_setAPI(long jarg1, CyberDeviceAPIEventHandler jarg1_, long jarg2);
  public final static native boolean CyberDeviceAPIEventHandler_onActivated(long jarg1, CyberDeviceAPIEventHandler jarg1_);
  public final static native boolean CyberDeviceAPIEventHandler_onDeactivated(long jarg1, CyberDeviceAPIEventHandler jarg1_);
  public final static native boolean CyberDeviceAPIEventHandler_onStartup(long jarg1, CyberDeviceAPIEventHandler jarg1_);
  public final static native boolean CyberDeviceAPIEventHandler_onShutdown(long jarg1, CyberDeviceAPIEventHandler jarg1_);
  public final static native boolean CyberDeviceAPIEventHandler_onError(long jarg1, CyberDeviceAPIEventHandler jarg1_);
  public final static native boolean CyberDeviceAPIEventHandler_onExecute(long jarg1, CyberDeviceAPIEventHandler jarg1_);
  public final static native void CyberDeviceAPIEventHandler_onObjectSelected(long jarg1, CyberDeviceAPIEventHandler jarg1_, long jarg2, long jarg3);
  public final static native void CyberDeviceAPIEventHandler_onSelfSelected(long jarg1, CyberDeviceAPIEventHandler jarg1_, long jarg2, long jarg3);
  public final static native void CyberDeviceAPIEventHandler_onFunctionCalled(long jarg1, CyberDeviceAPIEventHandler jarg1_, long jarg2, long jarg3);
  public final static native void CyberDeviceAPIEventHandler_onCollision(long jarg1, CyberDeviceAPIEventHandler jarg1_, long jarg2, long jarg3, long jarg4, long jarg5);
  public final static native boolean CyberDeviceAPIEventHandler_onMessageReceived(long jarg1, CyberDeviceAPIEventHandler jarg1_, int jarg2);
  public final static native void delete_CyberDeviceAPIEventHandler(long jarg1);
  public final static native long new_VirCADevice();
  public final static native void delete_VirCADevice(long jarg1);
  public final static native boolean VirCADevice_onActivated(long jarg1, VirCADevice jarg1_);
  public final static native boolean VirCADevice_onActivatedSwigExplicitVirCADevice(long jarg1, VirCADevice jarg1_);
  public final static native boolean VirCADevice_onDeactivated(long jarg1, VirCADevice jarg1_);
  public final static native boolean VirCADevice_onDeactivatedSwigExplicitVirCADevice(long jarg1, VirCADevice jarg1_);
  public final static native boolean VirCADevice_onExecute(long jarg1, VirCADevice jarg1_);
  public final static native boolean VirCADevice_onExecuteSwigExplicitVirCADevice(long jarg1, VirCADevice jarg1_);
  public final static native boolean VirCADevice_onStartup(long jarg1, VirCADevice jarg1_);
  public final static native boolean VirCADevice_onStartupSwigExplicitVirCADevice(long jarg1, VirCADevice jarg1_);
  public final static native void VirCADevice_registerCyberDevice(long jarg1, VirCADevice jarg1_, String jarg2, String jarg3);
  public final static native void VirCADevice_setCyberDevicePosition(long jarg1, VirCADevice jarg1_, String jarg2, float jarg3, float jarg4, float jarg5);
  public final static native void VirCADevice_print(long jarg1, VirCADevice jarg1_);
  public final static native void VirCADevice_printSwigExplicitVirCADevice(long jarg1, VirCADevice jarg1_);
  public final static native void VirCADevice_onObjectSelected(long jarg1, VirCADevice jarg1_, long jarg2, long jarg3);
  public final static native void VirCADevice_onObjectSelectedSwigExplicitVirCADevice(long jarg1, VirCADevice jarg1_, long jarg2, long jarg3);
  public final static native void VirCADevice_onFunctionCalled(long jarg1, VirCADevice jarg1_, long jarg2, long jarg3);
  public final static native void VirCADevice_onFunctionCalledSwigExplicitVirCADevice(long jarg1, VirCADevice jarg1_, long jarg2, long jarg3);
  public final static native void VirCADevice_onCollision(long jarg1, VirCADevice jarg1_, long jarg2, long jarg3, long jarg4, long jarg5);
  public final static native void VirCADevice_onCollisionSwigExplicitVirCADevice(long jarg1, VirCADevice jarg1_, long jarg2, long jarg3, long jarg4, long jarg5);
  public final static native void VirCADevice_director_connect(VirCADevice obj, long cptr, boolean mem_own, boolean weak_global);
  public final static native void VirCADevice_change_ownership(VirCADevice obj, long cptr, boolean take_or_release);
  public final static native long VirCADevice_SWIGUpcast(long jarg1);

  public static boolean SwigDirector_VirCADevice_onActivated(VirCADevice self) {
    return self.onActivated();
  }
  public static boolean SwigDirector_VirCADevice_onDeactivated(VirCADevice self) {
    return self.onDeactivated();
  }
  public static boolean SwigDirector_VirCADevice_onStartup(VirCADevice self) {
    return self.onStartup();
  }
  public static boolean SwigDirector_VirCADevice_onShutdown(VirCADevice self) {
    return self.onShutdown();
  }
  public static boolean SwigDirector_VirCADevice_onError(VirCADevice self) {
    return self.onError();
  }
  public static boolean SwigDirector_VirCADevice_onExecute(VirCADevice self) {
    return self.onExecute();
  }
  public static void SwigDirector_VirCADevice_onObjectSelected(VirCADevice self, long name, long location) {
    self.onObjectSelected(new SWIGTYPE_p_std__string(name, false), new SWIGTYPE_p_Ogre__Vector3(location, false));
  }
  public static void SwigDirector_VirCADevice_onSelfSelected(VirCADevice self, long name, long location) {
    self.onSelfSelected(new SWIGTYPE_p_std__string(name, false), new SWIGTYPE_p_Ogre__Vector3(location, false));
  }
  public static void SwigDirector_VirCADevice_onFunctionCalled(VirCADevice self, long name, long command) {
    self.onFunctionCalled(new SWIGTYPE_p_std__string(name, false), new SWIGTYPE_p_std__string(command, false));
  }
  public static void SwigDirector_VirCADevice_onCollision(VirCADevice self, long object1, long position1, long object2, long position2) {
    self.onCollision(new SWIGTYPE_p_std__string(object1, false), new SWIGTYPE_p_Ogre__Vector3(position1, false), new SWIGTYPE_p_std__string(object2, false), new SWIGTYPE_p_Ogre__Vector3(position2, false));
  }
  public static boolean SwigDirector_VirCADevice_onMessageReceived(VirCADevice self, int message) {
    return self.onMessageReceived(message);
  }
  public static void SwigDirector_VirCADevice_print(VirCADevice self) {
    self.print();
  }

  private final static native void swig_module_init();
  static {
    swig_module_init();
  }
}
