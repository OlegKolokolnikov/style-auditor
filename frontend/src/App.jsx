import { useState } from "react";
import InputPanel from "./components/InputPanel";
import Legend from "./components/Legend";
import SummaryBox from "./components/SummaryBox";
import ChunkList from "./components/ChunkList";
import ChunkDetail from "./components/ChunkDetail";
import LoadingSkeleton from "./components/LoadingSkeleton";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api/analyze";

export default function App() {
  const [text, setText] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [activeChunk, setActiveChunk] = useState(null);
  const [error, setError] = useState(null);
  const [isStale, setIsStale] = useState(false);

  async function analyze() {
    if (!text.trim()) return;
    setLoading(true);
    setError(null);

    try {
      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text }),
      });

      if (!res.ok) throw new Error(`Ошибка API: ${res.status}`);

      const data = await res.json();
      if (!data || !Array.isArray(data.chunks)) throw new Error("Неожиданный ответ от сервера");

      setResult(data);
      setActiveChunk(null);
      setIsStale(false);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  function handleTextChange(newText) {
    setText(newText);
    if (result) setIsStale(true);
  }

  function handleClear() {
    setText("");
    setResult(null);
    setActiveChunk(null);
    setIsStale(false);
    setError(null);
  }

  const shownChunks = result
    ? activeChunk === null
      ? result.chunks
      : result.chunks.filter((c) => c.index === activeChunk)
    : [];

  return (
    <main className="page">
      <h1>
        <img src="/logo.png" alt="логотип" className="siteLogo" />
        Аудит стиля (детектор графомании)
      </h1>

      <InputPanel
        text={text}
        setText={handleTextChange}
        onAnalyze={analyze}
        onClear={handleClear}
        loading={loading}
        error={error}
        onDismissError={() => setError(null)}
      />

      <Legend />

      {loading && <LoadingSkeleton />}

      {!loading && result && (
        <>
          <SummaryBox result={result} isStale={isStale} />

          <section className="layout">
            <ChunkList
              chunks={result.chunks}
              activeChunk={activeChunk}
              onSelect={setActiveChunk}
            />
            <section className="chunks">
              {shownChunks.map((chunk) => (
                <ChunkDetail key={chunk.index} chunk={chunk} />
              ))}
            </section>
          </section>
        </>
      )}

      <footer className="siteFooter">Сайт не несёт ответственности ни за что.</footer>
    </main>
  );
}
