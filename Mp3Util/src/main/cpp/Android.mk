LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LAME_LIBMP3_DIR := lame

LOCAL_MODULE    := mp3lame

LOCAL_SRC_FILES :=\
$(LAME_LIBMP3_DIR)/bitstream.c \
$(LAME_LIBMP3_DIR)/fft.c \
$(LAME_LIBMP3_DIR)/id3tag.c \
$(LAME_LIBMP3_DIR)/mpglib_interface.c \
$(LAME_LIBMP3_DIR)/presets.c \
$(LAME_LIBMP3_DIR)/quantize.c \
$(LAME_LIBMP3_DIR)/reservoir.c \
$(LAME_LIBMP3_DIR)/tables.c  \
$(LAME_LIBMP3_DIR)/util.c \
$(LAME_LIBMP3_DIR)/VbrTag.c \
$(LAME_LIBMP3_DIR)/encoder.c \
$(LAME_LIBMP3_DIR)/gain_analysis.c \
$(LAME_LIBMP3_DIR)/lame.c \
$(LAME_LIBMP3_DIR)/newmdct.c \
$(LAME_LIBMP3_DIR)/psymodel.c \
$(LAME_LIBMP3_DIR)/quantize_pvt.c \
$(LAME_LIBMP3_DIR)/set_get.c \
$(LAME_LIBMP3_DIR)/takehiro.c \
$(LAME_LIBMP3_DIR)/vbrquantize.c \
$(LAME_LIBMP3_DIR)/version.c \
MP3Encoder.c

include $(BUILD_SHARED_LIBRARY)
