/*
 * Copyright (c) 2026 MirraNET, Niklas Linz. All rights reserved.
 *
 * This file is part of the MirraNET project and is licensed under the
 * GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You may use, distribute and modify this code under the terms
 * of the LGPLv3 license. You should have received a copy of the
 * license along with this file. If not, see <https://www.gnu.org/licenses/lgpl-3.0.html>
 * or contact: niklas.linz@mirranet.de
 */

package de.linzn.evolutionApiJava.api;

/**
 * Media of a WhatsApp message (image, document, audio, video or sticker).
 *
 * @param type     image, document, audio, video or sticker
 * @param mimeType MIME type as sent by WhatsApp, e.g. {@code image/jpeg}; may be null
 * @param fileName file name of documents; null for most other media
 * @param size     size in bytes, 0 if unknown
 * @param voice    true for recorded voice messages (push to talk)
 * @param base64   content as base64 if the webhook delivered it (Evolution setting "webhook base64"), otherwise null;
 *                 use {@link de.linzn.evolutionApiJava.api.web.WebApiProvider#downloadMedia(TextMessage)} to load it
 */
public record MediaAttachment(String type, String mimeType, String fileName, long size, boolean voice, String base64) {

    public boolean hasData() {
        return this.base64 != null && !this.base64.isEmpty();
    }

    public MediaAttachment withBase64(String data, String newMimeType, String newFileName) {
        return new MediaAttachment(this.type,
                newMimeType != null && !newMimeType.isBlank() ? newMimeType : this.mimeType,
                this.fileName != null ? this.fileName : newFileName,
                this.size, this.voice, data);
    }
}
