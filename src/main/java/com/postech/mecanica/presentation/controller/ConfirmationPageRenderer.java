package com.postech.mecanica.presentation.controller;

public final class ConfirmationPageRenderer {

    private ConfirmationPageRenderer() {
    }

    public static String renderizar(String titulo, String subtitulo, String status, Long id, boolean sucesso) {
        String corPrimaria = sucesso ? "#16a34a" : "#dc2626";
        String icone = sucesso ? "✓" : "✕";

        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>%s</title>
              <style>
                body {
                  margin: 0;
                  padding: 24px;
                  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                  background-color: #f1f5f9;
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  min-height: 100vh;
                  box-sizing: border-box;
                }
                .card {
                  background: #ffffff;
                  max-width: 520px;
                  width: 100%%;
                  padding: 40px 32px;
                  border-radius: 16px;
                  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
                  text-align: center;
                }
                .icon-circle {
                  width: 72px;
                  height: 72px;
                  border-radius: 50%%;
                  background-color: %s;
                  color: #ffffff;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  font-size: 36px;
                  font-weight: bold;
                  margin: 0 auto 20px auto;
                }
                h1 {
                  font-size: 22px;
                  color: #0f172a;
                  margin: 0 0 12px 0;
                }
                p {
                  font-size: 15px;
                  color: #64748b;
                  line-height: 1.6;
                  margin: 0 0 24px 0;
                }
                .badge {
                  display: inline-block;
                  padding: 6px 16px;
                  background-color: #f8fafc;
                  border: 1px solid #e2e8f0;
                  border-radius: 9999px;
                  font-size: 13px;
                  font-weight: 600;
                  color: #334155;
                }
              </style>
            </head>
            <body>
              <div class="card">
                <div class="icon-circle">%s</div>
                <h1>%s</h1>
                <p>%s</p>
                <div class="badge">Ordem de Serviço #%d • Status: %s</div>
              </div>
            </body>
            </html>
            """.formatted(titulo, corPrimaria, icone, titulo, subtitulo, id, status);
    }
}
