# photobackup-server

## Supported platforms

For now, just FreeBSD

Linux, macOS, and Windows following soon.

FreeBSD will remain the suggested platform, though, as it has native support for ZFS.

Please feel free to add Solaris support in case you like ^^

It's a Java daemon but requires ImageMagick7 with HEIC support enabled.

## Installation - FreeBSD

### Create a ZFS mirror or Raid 6 with new hard disks, ideally not from the same batch

It's not straightforward to find out which batch they're from. It's better if they are
from different batches because that makes it less likely to fail around the same time.

BackBlaze has written a great blog post about hard disks, please use that as s starting
point, or ask people how are more knowledgable about these things.

### Alternatively, use a partition on your existing robust backup grade storage solution

In this case it matters a lot less whether you use FreeBSD as it's really all about the storage.

### Clone the project photobackup-server

It needs to exist so you can copy a shared library file into it.

Create the following sub-directories:

    assets
    photos
    thumbnails
    libraries

### Fetch and build ImageMagick7 with HEIC support.

The correct version is ImageMagick7.0.8-22.

    sudo portsnap fetch

installs all ports. They are relatively tiny, less than 1GB at the moment for all of them, as they only contain scripts, patches, &c. but not the entire source code.

go to 

    ls /usr/ports/graphics/ImageMagick7

and call

    sudo make config

The config dialog will allow you to enable HEIF support.

HEIF is the container format of HEIC files. I think ImageMagick uses ffmpeg for the decoding of the contained image data itself but I'm not sure.

HEIF is necessary for reading HEIC files, which is the current format of the iPhone's photos.

Please enable HEIF support.

Now, in case ImageMagick7 is already installed from a package (in which case it currently does not have HEIF enabled and thus can't read HEIC images), plase uninstall using

    sudo pkg remove ImageMagick7

Then, call

    sudo make install

to install your configured port of ImageMagick7.0.8-22.

### Clone and build the project photobackup-server-native

It contains the JNI code for accessing the ImageMagick7 libraries.

Build the project by calling

    ./buiild.sh

The .so / .dll file

    libphotobackup-server-native.so
    
or

    photobackup-server-native.dll

thus created in build/ needs to be copied in to the ```libraries``` sub-directory of the photobackup-server project.

### Go to the directory of the project photobackup-server

Start the server by calling gradle run.

It does not currently run in the background.

You need to use screen or tmux.

No, seriously. I am not joking.

nohup doesn't seem to wirk with gradle.

Finding out how to run in in background is the very next thing I'm going to do ^^

Even before doing the android client.

### On a mac, build and install the photobackup iOS client

I will put it on the app store. But right now, you still have to change the hard-coded server host address.

Again, I am not joking. I will finish the settings dialog in between making gradle run in background and the android client.

### Use the browser to view your photos

Currently, the server still listens on port 8080 only. I will make that configurable at the same time as making it run in background.

But before any of that, I will create issues for alll these things here in this github repository, including for photobackup-server-native and the photobackup client.
