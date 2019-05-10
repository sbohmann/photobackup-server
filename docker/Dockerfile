# ATTENTION!!! This version of the Dockerfile still uses ephemeral storage only
# DO NOT USE for real backups, as they will be TEMPORARY!!!
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
RUN apt-get -y install cmake
RUN ln -s /usr/local/lib/libMagickWand-7.Q16HDRI.so /usr/local/lib/libMagickWand-7.so
RUN git clone https://github.com/sbohmann/photobackup-server-native.git && cd photobackup-server-native && JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64/" JNI_OS="linux" ./build.sh
RUN git clone https://github.com/sbohmann/photobackup-server.git && cd photobackup-server && mkdir libraries && cp ../photobackup-server-native/build/libphotobackup_server_native.so libraries/ && gradle -no-daemon build
RUN cd photobackup-server && git pull
CMD cd photobackup-server && ./create_links.sh /storage && version=$(gradle -no-daemon properties -q | grep "version:" | awk '{print $2}') && java -Djava.library.path="libraries" -jar "build/libs/photobackup-server-${version}.jar"
