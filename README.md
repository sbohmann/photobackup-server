# photobackup-server

A Photo Backup Server

Entirely file system based backups of photos and videos from iOS including all resources,
including original, unedited version, editing plists, &c.

An asset catalog containing metadata is stored as JSON files - historical as well as consolidated.

All resources are identified and stored by their SHA-512 keys.

The server's web gallery creates JPEG vrsions of HEIC and other non-JPEG image files on demand and
as thumbnails.

All resources and assets are accessible via a REST API as well, including JPEG conversions and thumbnails.

No information at all is stored in an opaque manner.

Everything is stored in local JSON and image files, except non-image resources from photo backups,
like plists (which are just XML).

The ImageMagick 7 library is used to convert photos and create thumbnails.

## Supported platforms

For now, just FreeBSD.

Linux, macOS, and Windows following soon.

FreeBSD will remain the suggested platform, though, as it has native support for ZFS.

Support for Solaris is not planned but it should be fairly easy to get it running there.

It's a Java daemon but requires ImageMagick7 with HEIC support enabled.

## Installation - FreeBSD

### Create a ZFS mirror or Raid 6 with new hard disks, ideally not from the same batch

It's not straightforward to find out which batch they're from. It's better if they are
from different batches because that makes them less likely to fail around the same time.

BackBlaze has written a great blog post about hard disks, please use that as a starting
point, or ask people how are more knowledgable about these things.

Please, do not forget to add the line

    zfs_enable="YES"

to ``/etc/rc.conf``, if missing. Otherwise the zpool will not be mounted on reboot. I'm just
mentioning this because I had made that mistake the first time around.

Plus, as I understand it, there needs to be no directory created in your root file system
as a mount point, as it will implicitly be there, in which case there can be no ambiguity
about where files end up. Maybe it just creates it while mounting, in which case that
advantage disappears. I really do not know much about these details, please consult the
FreeBSD and ZFS documentation about all of this.

### Alternatively, use a partition on your existing robust backup grade storage solution

In this case it matters a lot less whether you use FreeBSD as it's really all about the storage.

### Clone the project photobackup-server

It needs to exist so you can copy a shared library file into it.

Create the following sub-directories:

    assets
    upload
    photos
    thumbnails
    videos
    libraries

### Fetch and build ImageMagick7 with HEIC support.

The correct version is ImageMagick7.0.8-22.

    sudo portsnap fetch
    sudo portsnap extract

installs or updates all ports. They are relatively tiny, less than 1GB at the moment for all of them, as they only contain scripts, patches, &c. but not the entire source code.

Then, go to 

    ls /usr/ports/graphics/ImageMagick7

and call

    sudo make config

The config dialog will allow you to enable HEIF support.

HEIF is the container format of HEIC files. I think ImageMagick uses ffmpeg for the decoding of the contained image data itself but I'm not sure.

HEIF is necessary for reading HEIC files, which is the current format of the iPhone's photos.

Please enable HEIF support.

Now, in case ImageMagick7 is already installed from a package (in which case it currently does not have HEIF enabled and thus can't read HEIC images), plase uninstall it using

    sudo pkg remove ImageMagick7

Then, call

    sudo make install

from the current directory

    /usr/ports/graphics/ImageMagick7

in order to install your freshly configured port of ImageMagick7.0.8-22, which will have HEIF support enabled and thus can read HEIC files.

### Clone and build the project photobackup-server-native

It contains the JNI code necessary for accessing the native ImageMagick7 libraries from Java.

Build the project by calling

    ./buiild.sh

The .so / .dll file

    libphotobackup-server-native.so
    
or

    photobackup-server-native.dll

thus created in the sub-directory

    build/

needs to be copied in to the

    libraries

sub-directory of the ```photobackup-server``` project.

### Go to the directory of the project photobackup-server

Start the server by running the executable jar from the directory of ```photobackup-server``` project:

    java -Djava.library.path="libraries" build/libs/photobackup-server-<version>.jar

You have all the usual options, like running it in the foreground, with screen or tmux, with nohup, or as a proper daemon, including via the boot process.

The execution directory must be that of the ```photobackup-server``` project n all cases.

PLease, run it as a user with just minimum necessary privileges in all these cases, and consider making it only indirectly accessible by e.g. putting an nginex or similar in front of it and making it only locally accessible.

Please, consider sources and literature more knowledge about the security issues of running a spring boot mvc server in your environment.

### On a mac, build and install the project photobackup

This project contains the iOS client.

I will put it on the app store soon but right now, the only way to install it is to build it yourself.

### Use the browser to view your photos

Currently, the server still listens on port 8080 only. I will make that configurable at the same time as making it run in background.

The photos are in the path

    /gallery

but as you probably do not want to see all of them at once, you can pick a date, e.g.

    /gallery/2019-01-31

for just the photos from the 31st of January, 2019.

Navigation, month views, &c. are coming soon.

### Why isn't there a comfortable installer?

There isn't a comfortable installer *yet* 😎

I will probably do it in bash, even though bash isn't strictly necessary for something like this, as sh is theoretically sufficient. It's just *better* for complex scripting 🙂
