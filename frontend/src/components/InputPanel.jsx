import { Upload } from "lucide-react";

const MAX_CHARS = 200_000;

export default function InputPanel({ text, setText, onAnalyze, onClear, loading, error, onDismissError, onError }) {
  const charCount = text.length;
  const wordCount = text.trim().split(/\s+/).filter(Boolean).length;
  const tooLong = charCount > MAX_CHARS;
  const tooShort = wordCount < 50;

  async function handleFileUpload(event) {
    const file = event.target.files?.[0];
    if (!file) return;
    event.target.value = "";
    // 600KB covers 200k Russian chars even at 3 bytes/char (UTF-8)
    if (file.size > 600_000) {
      onError("Файл слишком большой. Максимум — 200 000 символов.");
      return;
    }
    const content = await file.text();
    setText(content);
  }

  return (
    <section className="inputPanel">
      <div className="toolbar">
        <label className="fileButton">
          <Upload size={18} />
          Загрузить .txt
          <input type="file" accept=".txt,text/plain" onChange={handleFileUpload} />
        </label>

        <button onClick={onAnalyze} disabled={loading || tooShort || tooLong}>
          {loading ? "Проверяю..." : "Проверить"}
        </button>

        {text && (
          <button onClick={onClear}>Очистить</button>
        )}

        {tooShort && wordCount > 0 && (
          <span className="wordCount">{wordCount} / 50 слов</span>
        )}
        {tooLong && (
          <span className="wordCount error">{charCount.toLocaleString("ru")} / 200 000 симв.</span>
        )}
      </div>

      {error && (
        <div className="errorBanner">
          {error}
          <button className="errorDismiss" onClick={onDismissError}>✕</button>
        </div>
      )}

      <textarea
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Вставьте текст или загрузите .txt..."
      />
    </section>
  );
}
