package com.shop.common.exception;

/**
 * Mot de passe actuel erroné lors d'un changement d'identifiants. Traduit en 400 (et non 401) : la
 * session reste valide, seul le formulaire est en erreur — un 401 déclencherait côté front la
 * logique de refresh/déconnexion.
 */
public class InvalidCurrentPasswordException extends RuntimeException {

  public InvalidCurrentPasswordException() {
    super("Current password is incorrect");
  }
}
