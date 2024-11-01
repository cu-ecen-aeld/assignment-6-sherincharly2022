# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# TODO: Set this  with the path to your assignments rep.  Use ssh protocol and see lecture notes
# about how to setup ssh-agent for passwordless access
SRC_URI = "git://git@github.com/cu-ecen-aeld/assignments-3-and-later-sherincharly2022;protocol=ssh;branch=assignment6-part2"

PV = "1.0+git${SRCPV}"
# TODO: set to reference a specific commit hash in your assignment repo
SRCREV = "c72af15b61122108414e0a59483a3a2279565ab8"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at 
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# We reference the "server" directory here to build from the "server" directory
# in your assignments repo
S = "${WORKDIR}/git"

# TODO: Add the aesdsocket application and any other files you need to install
# See https://git.yoctoproject.org/poky/plain/meta/conf/bitbake.conf?h=kirkstone
FILES:${PN} += "${bindir}/aesdsocket"

# TODO: customize these as necessary for any libraries you need for your application
# (and remove comment)
TARGET_LDFLAGS += "-pthread -lrt"

inherit update-rc.d

INITSCRIPT_NAME = "S99aesdsocket"
INITSCRIPT_PARAMS = "defaults"

do_configure () {
	# $(MAKE) $(TARGET_CONFIGURE_OPTS) -C ${D}/server all
	:
}

do_compile () {
	oe_runmake 'CC=${CC}' 'TARGET_LDFLAGS=${TARGET_LDFLAGS}' -C ${S}/server all
}

do_install () {
	# TODO: Install your binaries/scripts here.
	# Be sure to install the target directory with install -d first
	# Yocto variables ${D} and ${S} are useful here, which you can read about at 
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-D
	# and
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-S
	# See example at https://github.com/cu-ecen-aeld/ecen5013-yocto/blob/ecen5013-hello-world/meta-ecen5013/recipes-ecen5013/ecen5013-hello-world/ecen5013-hello-world_git.bb
	# $(INSTALL) -m 0755 ${D}/server/aesdsocket $(TARGET_DIR)/usr/bin/
	install -d ${D}${bindir}
	install -d ${D}${sysconfdir}/init.d
	# install -m 0755 ${WORKDIR}/assignment-autotest/test/assignment6-yocto/* ${D}${bindir}
	install -m 0755 ${S}/server/aesdsocket ${D}${bindir}/
	install -m 0755 ${S}/server/aesdsocket-start-stop ${D}/${sysconfdir}/init.d/S99aesdsocket
}

pkg_postinst_ontarget_${PN} () {
    # Your post-install commands here
    # For example, if you need to run a script:
    if [ -n "$D" ]; then
        exit 1
    fi
    /etc/init.d/S99aesdsocket start
}


FILES:${PN} += "${sysconfdir}/init.d/S99aesdsocket"
