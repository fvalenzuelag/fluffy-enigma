const express = require("express");
const path = require("path");
const { createProxyMiddleware } = require("http-proxy-middleware");

const app = express();
const port = process.env.PORT || 3000;

const javaUrl = process.env.JAVA_API_URL || "http://localhost:8080";
const pythonUrl = process.env.PYTHON_API_URL || "http://localhost:5000";

// El navegador llama al mismo origen (Node); Node reenvía a los servicios por nombre en Docker Compose.
app.use(
  "/api/java",
  createProxyMiddleware({
    target: javaUrl,
    changeOrigin: true,
    pathRewrite: { "^/api/java": "" },
  })
);

app.use(
  "/api/python",
  createProxyMiddleware({
    target: pythonUrl,
    changeOrigin: true,
    pathRewrite: { "^/api/python": "" },
  })
);

app.use(express.static(path.join(__dirname, "public")));

app.listen(port, "0.0.0.0", () => {
  console.log(`Frontend en http://0.0.0.0:${port}`);
});
