/**
 * API mínima de películas: datos solo en memoria (sin base de datos).
 * Puerto 3000 — pensado para ejecutarse dentro del contenedor Docker.
 */
import express from "express";

const app = express();
app.use(express.json());

const movies = [
  { id: 1, title: "Matrix", year: 1999 },
  { id: 2, title: "Interestelar", year: 2014 },
  { id: 3, title: "Blade Runner 2049", year: 2017 },
];

app.get("/movies", (_req, res) => {
  res.json(movies);
});

app.get("/movies/:id", (req, res) => {
  const id = Number(req.params.id);
  const movie = movies.find((m) => m.id === id);
  if (!movie) {
    return res.status(404).json({ error: "Película no encontrada" });
  }
  res.json(movie);
});

const port = Number(process.env.PORT) || 3000;
app.listen(port, "0.0.0.0", () => {
  console.log(`Películas API escuchando en 0.0.0.0:${port}`);
});
