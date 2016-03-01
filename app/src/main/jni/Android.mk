LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog
LOCAL_MODULE    := movementSecurity
LOCAL_SRC_FILES := movementSecurity.c
include $(BUILD_SHARED_LIBRARY)
