package com.shop.common.exception;

/**
 * Exception générique pour les conflits métier (slug/sku déjà utilisé, suppression d'une ressource
 * encore référencée, etc.) — évite de créer une classe dédiée pour chaque cas quand le message
 * suffit à qualifier le conflit.
 */
public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }
}
