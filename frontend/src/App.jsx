import { useState } from "react";
import { Upload } from "lucide-react";

const API_URL = "http://localhost:8080/api/analyze";

export default function App() {
  const [text, setText] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [activeChunk, setActiveChunk] = useState(null);

  async function analyze() {
    if (!text.trim()) return;

    setLoading(true);

    try {
      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text })
      });

      if (!res.ok) {
        throw new Error(`Ошибка API: ${res.status}`);
      }

      const data = await res.json();
      setResult(data);
      setActiveChunk(null);
    } catch (e) {
      alert(e.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleFileUpload(event) {
    const file = event.target.files?.[0];
    if (!file) return;

    const content = await file.text();
    setText(content);
    setResult(null);
    setActiveChunk(null);
  }

  const shownChunks = result
      ? activeChunk === null
          ? result.chunks
          : result.chunks.filter((chunk) => chunk.index === activeChunk)
      : [];

  return (
      <main className="page">
        <h1>Стилистический аудитор</h1>

        <section className="inputPanel">
          <div className="toolbar">
            <label className="fileButton">
              <Upload size={18} />
              Загрузить .txt
              <input
                  type="file"
                  accept=".txt,text/plain"
                  onChange={handleFileUpload}
              />
            </label>

            <button onClick={analyze} disabled={loading || !text.trim()}>
              {loading ? "Проверяю..." : "Проверить"}
            </button>
          </div>

          <textarea
              value={text}
              onChange={(e) => {
                setText(e.target.value);
                setResult(null);
                setActiveChunk(null);
              }}
              placeholder="Вставьте текст или загрузите .txt..."
          />
        </section>

        {result && (
            <>
              {/* ===== SUMMARY В РАМКЕ ===== */}
              <section className="summaryBox">
                <h2>{result.smoothnessLabel}</h2>

                <p className="summaryVerdict">
                  {result.verdict}
                </p>

                <p className="summaryNote">
                  Индекс отражает сглаженность текста, а не «силу ошибки».
                </p>
              </section>

              <section className="layout">
                <aside className="chunkList">
                  <h2>Чанки</h2>

                  {result.chunks.map((chunk) => (
                      <button
                          key={chunk.index}
                          className={`chunkButton ${riskClass(chunk.riskScore)} ${
                              activeChunk === chunk.index ? "active" : ""
                          }`}
                          onClick={() =>
                              setActiveChunk(
                                  activeChunk === chunk.index ? null : chunk.index
                              )
                          }
                      >
                        <span>#{chunk.index + 1}</span>
                        <b>индекс {chunk.riskScore}/100</b>
                        <small>{chunk.label}</small>
                      </button>
                  ))}
                </aside>

                <section className="chunks">
                  {shownChunks.map((chunk) => (
                      <article key={chunk.index} className="chunk">
                        <header className="chunkHeader">
                          <div>
                            <h3>Чанк #{chunk.index + 1}</h3>
                            <p className="meta">
                              {chunk.chars} симв. · {chunk.sentenceCount} предл. ·
                              средняя длина {chunk.avgSentenceLength} · разброс{" "}
                              {chunk.sentenceLengthStd}
                            </p>
                          </div>

                          <div className={`riskBadge ${riskClass(chunk.riskScore)}`}>
                            <small>индекс чанка</small>
                            <br />
                            <b>{chunk.riskScore}/100</b>
                          </div>
                        </header>

                        <div className="meters">
                          <Meter
                              label="Ожидаемость лексики"
                              value={chunk.lexicalPredictability}
                          />
                          <Meter
                              label="Монотонность ритма"
                              value={chunk.rhythmMonotony}
                          />
                        </div>

                        {chunk.flags?.length > 0 ? (
                            <div className="flags">
                              {chunk.flags.map((flag, index) => (
                                  <div
                                      key={index}
                                      className={`flag ${flag.severity || "medium"}`}
                                      style={flagStyle(flag.severity)}
                                  >
                                    <div>
                                      <b>{flag.type}</b>
                                      <span>{flag.count} найдено</span>
                                    </div>
                                    <p>{flag.comment}</p>
                                  </div>
                              ))}
                            </div>
                        ) : (
                            <div className="clean">
                              Сильных формальных флагов нет.
                            </div>
                        )}

                        {chunk.suggestions?.length > 0 && (
                            <div className="suggestions">
                              <h3>Что можно проверить</h3>
                              <ul>
                                {chunk.suggestions.map((suggestion, index) => (
                                    <li key={index}>{suggestion}</li>
                                ))}
                              </ul>
                            </div>
                        )}

                        <div className="textBlock">
                          {renderHighlightedText(chunk)}
                        </div>
                      </article>
                  ))}
                </section>
              </section>
            </>
        )}
      </main>
  );
}

function Meter({ label, value }) {
  const safeValue = Number.isFinite(Number(value)) ? Number(value) : 0;
  const width = Math.min(100, Math.max(0, safeValue));

  return (
      <div className="meter">
        <div className="meterTop">
          <span>{label}</span>
          <b>{safeValue}/100</b>
        </div>
        <div className="meterBar">
          <div style={{ width: `${width}%` }} />
        </div>
      </div>
  );
}

function renderHighlightedText(chunk) {
  const highlights = chunk.highlights || [];

  if (!highlights.length) {
    return chunk.text;
  }

  const sorted = [...highlights]
      .filter((h) => h.start >= 0 && h.end > h.start)
      .sort((a, b) => a.start - b.start);

  const parts = [];
  let cursor = 0;

  sorted.forEach((highlight, index) => {
    if (highlight.start < cursor) return;

    if (highlight.start > cursor) {
      parts.push(chunk.text.slice(cursor, highlight.start));
    }

    parts.push(
        <mark
            key={`${highlight.start}-${highlight.end}-${index}`}
            className={`highlight ${highlight.severity || "medium"}`}
            title={highlight.type}
        >
          {chunk.text.slice(highlight.start, highlight.end)}
        </mark>
    );

    cursor = highlight.end;
  });

  if (cursor < chunk.text.length) {
    parts.push(chunk.text.slice(cursor));
  }

  return parts;
}

function riskClass(score) {
  if (score >= 60) return "high";
  if (score >= 30) return "medium";
  return "low";
}

function flagStyle(severity) {
  if (severity === "high" || severity === "red") {
    return {
      background: "rgba(239, 68, 68, 0.14)",
      border: "1px solid rgba(239, 68, 68, 0.45)",
      color: "#111827"
    };
  }

  if (severity === "medium" || severity === "yellow") {
    return {
      background: "rgba(245, 158, 11, 0.18)",
      border: "1px solid rgba(245, 158, 11, 0.45)",
      color: "#111827"
    };
  }

  return {
    background: "rgba(34, 197, 94, 0.14)",
    border: "1px solid rgba(34, 197, 94, 0.45)",
    color: "#111827"
  };
}