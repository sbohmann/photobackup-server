# Unfinished - the big part that's missing is installing ImageMagick with all required features.
# Once that's done, I will add the easy part, which is fetching from git (via RUN) and running with gradle (via CMD) &c.
FROM debian:testing
RUN echo "deb-src http://deb.debian.org/debian testing main" >> /etc/apt/sources.list
RUN apt-get update && apt-get -y install git openjdk-11-jdk-headless wget unzip build-essential libde265-dev libheif-dev
RUN wget https://imagemagick.org/download/ImageMagick.tar.gz
RUN apt-get -y build-dep imagemagick
# TODO verify checksum in order to guard against accidental errors
RUN tar xvzf ImageMagick.tar.gz && cd ImageMagick-7.* && ./configure --with-heic=yes && make
RUN cd ImageMagick-7.* && make install
RUN apt-get -y install ffmpeg
RUN wget https://services.gradle.org/distributions/gradle-5.4-bin.zip && mkdir /opt/gradle && unzip -d /opt/gradle gradle-5.4-bin.zip
ENV PATH="$PATH:/opt/gradle/gradle-5.4/bin"
RUN git clone https://github.com/sbohmann/photobackup-server.git && cd photobackup-server && mkdir assets upload photos thumbnails videos libraries
RUN apt-get -y install cmake
RUN ln -s /usr/local/lib/libMagickWand-7.Q16HDRI.so /usr/local/lib/libMagickWand-7.so
RUN git clone https://github.com/sbohmann/photobackup-server-native.git && cd photobackup-server-native && JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64/" JNI_OS="linux" ./build.sh && cp build/libphotobackup_server_native.so ../photobackup-server/libraries/
#RUN cd photobackup-server && git pull && gradle -no-daemon test
#CMD cd photobackup-server && git pull && gradle -no-daemon run
