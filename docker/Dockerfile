# Unfinished - the big part that's missing is installing ImageMagick with all required features.
# Once that's done, I will add the easy part, which is fetching from git (via RUN) and running with gradle (via CMD) &c.
FROM debian:testing
RUN echo "deb-src http://deb.debian.org/debian testing main" >> /etc/apt/sources.list
RUN apt-get update && apt-get -y install git openjdk-11-jdk-headless wget unzip build-essential libde265-dev libheif-dev
RUN wget https://imagemagick.org/download/ImageMagick.tar.gz
RUN apt-get build-dep imagemagick
# TODO verify checksum in order to guard against accidental errors
RUN tar xvzf ImageMagick.tar.gz && cd ImageMagick-7.* && ./configure --with-heic=yes && make
#RUN wget https://services.gradle.org/distributions/gradle-5.4-bin.zip && mkdir /opt/gradle && unzip -d /opt/gradle gradle-5.4-bin.zip
#ENV PATH="$PATH:/opt/gradle/gradle-5.4/bin"
#CMD echo Hi! && java -version && git --version && gradle -version
#CMD /bin/sh
