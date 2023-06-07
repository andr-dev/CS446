window.onload = function () {
  window.ui = SwaggerUIBundle({
    url: "api/openapi.json",
    dom_id: "#swagger-ui",
    deepLinking: true,
    presets: [SwaggerUIBundle.presets.apis],
    plugins: [SwaggerUIBundle.plugins.DownloadUrl],
  });
};
