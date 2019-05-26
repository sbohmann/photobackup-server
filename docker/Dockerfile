FROM debian:testing
RUN echo "deb-src http://deb.debian.org/debian testing main" >> /etc/apt/sources.list
RUN apt-get update && apt-get -y install git openjdk-11-jdk-headless wget unzip build-essential libde265-dev libheif-dev ffmpeg cmake && apt-get -y build-dep imagemagick
RUN wget https://imagemagick.org/download/ImageMagick.tar.gz
# TODO verify checksum in order to guard against accidental errors
RUN tar xvzf ImageMagick.tar.gz && cd ImageMagick-7.* && ./configure --with-heic=yes && num_threads="$(($(nproc)+1))" && echo "$num_threads threads used for make" && make -j "$num_threads" && make install
RUN wget https://services.gradle.org/distributions/gradle-5.4-bin.zip && mkdir /opt/gradle && unzip -d /opt/gradle gradle-5.4-bin.zip
ENV PATH="$PATH:/opt/gradle/gradle-5.4/bin"
RUN git clone https://github.com/sbohmann/photobackup-server-native.git
RUN git clone https://github.com/sbohmann/photobackup-server.git && cd photobackup-server && mkdir libraries
ENV photobackup_server_native_version="1.0.0"
RUN cd photobackup-server-native && git fetch && git checkout "tags/$photobackup_server_native_version" && JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64/" JNI_OS="linux" MAGICK_WAND_LIBRARY="MagickWand-7.Q16HDRI" ./build.sh
ENV photobackup_server_version="1.1.0"
RUN cd photobackup-server && git fetch && git checkout "tags/$photobackup_server_version" && cp ../photobackup-server-native/build/libphotobackup_server_native.so libraries/ && gradle -no-daemon build
CMD cd photobackup-server && ./create_storage_links.sh /storage && ./run.sh
