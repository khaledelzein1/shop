export const environment = {
  production: true,
  // Chemin relatif : en prod, un reverse proxy (nginx) sert le build Angular
  // et redirige /api vers le backend — décision détaillée à l'étape DevOps.
  apiUrl: '/api',
};
