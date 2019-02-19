# photobackup-server

## Supported platforms

For now, just FreeBSD

Linux, macOS, and Windows following soon.

FreeBSD will remain the suggested platform, though, as it has native support for ZFS.

Please feel free to add Solaris support in case you like ^^

It's a Java daemon but requires ImageMagick7 with HEIC support enabled.

## Installation- FreeBSD

### Create a ZFS mirror or Raid 6 with new hard disks, ideally not from the same batch

It's not straightforward to find out which batch they're from. It's better if they are
from different batches because that makes it less likely to fail around the same time.

BackBlaze has written a great blog post about hard disks, please use that as s starting
point, or ask people how are more knowledgable about these things.

### Alternatively, use a partition on your existing robust backup grade storage solution

In this case it matters a lot less whether you use FreeBSD as it's really all about the storage.

