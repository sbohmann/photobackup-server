/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class at_yeoman_photobackup_server_imageMagick_ImageMagick */

#ifndef _Included_at_yeoman_photobackup_server_imageMagick_ImageMagick
#define _Included_at_yeoman_photobackup_server_imageMagick_ImageMagick
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     at_yeoman_photobackup_server_imageMagick_ImageMagick
 * Method:    initialize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_at_yeoman_photobackup_server_imageMagick_ImageMagick_initialize
  (JNIEnv *, jclass);

/*
 * Class:     at_yeoman_photobackup_server_imageMagick_ImageMagick
 * Method:    convertToJpeg
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_at_yeoman_photobackup_server_imageMagick_ImageMagick_convertToJpeg
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     at_yeoman_photobackup_server_imageMagick_ImageMagick
 * Method:    convertToJpegWithMaximumSize
 * Signature: ([BII)[B
 */
JNIEXPORT jbyteArray JNICALL Java_at_yeoman_photobackup_server_imageMagick_ImageMagick_convertToJpegWithMaximumSize
  (JNIEnv *, jclass, jbyteArray, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
