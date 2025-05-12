package io.github.pheonixhkbxoic.adk.message;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 21:50
 * @desc
 */
public interface MimeType {
    String TEXT = "text/plain";
    String TEXT_HTML = "text/html";
    String TEXT_CSS = "text/css";
    String TEXT_JAVASCRIPT = "text/javascript";

    String IMAGE_PNG = "image/png";
    String IMAGE_JPEG = "image/jpeg";
    String IMAGE_GIF = "image/gif";
    String IMAGE_BMP = "image/bmp";
    String IMAGE_SVG = "image/svg+xml";
    String IMAGE_WEBP = "image/webp";

    String VIDEO_WEBM = "video/webm";
    String VIDEO_MP4 = "video/mp4";
    String VIDEO_MPEG = "video/mpeg";
    String VIDEO_MOV = "video/quicktime";
    String VIDEO_M4V = "video/x-m4v";
    String VIDEO_AVI = "video/x-msvideo";
    String VIDEO_FLV = "video/x-flv";

    String AUDIO_MPEG = "audio/mpeg";
    String AUDIO_MIDI = "audio/midi";
    String AUDIO_WAV = "audio/x-wav";
    String AUDIO_M3U = "audio/x-mpegurl";
    String AUDIO_M4A = "audio/x-m4a";
    String AUDIO_OGG = "audio/ogg";
    String AUDIO_RA = "audio/x-realaudio";


    // other
    String JSON = "application/json";
    String PDF = "application/pdf";
    String DOC = "application/msword";
    String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    String XLS = "application/vnd.ms-excel";
    String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    String PPT = "application/vnd.ms-powerpoint";
    String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    String GZ = "application/gzip";
    String ZIP = "application/zip";
    String RAR = "application/rar";
    String TAR = "application/x-tar";

}
