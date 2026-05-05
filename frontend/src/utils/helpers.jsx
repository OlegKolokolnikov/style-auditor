export function riskClass(score) {
  if (score >= 60) return "high";
  if (score >= 30) return "medium";
  return "low";
}

export function flagStyle(severity) {
  if (severity === "high" || severity === "red") {
    return {
      background: "rgba(239, 68, 68, 0.14)",
      border: "1px solid rgba(239, 68, 68, 0.45)",
      color: "#111827",
    };
  }
  if (severity === "medium" || severity === "yellow") {
    return {
      background: "rgba(245, 158, 11, 0.18)",
      border: "1px solid rgba(245, 158, 11, 0.45)",
      color: "#111827",
    };
  }
  return {
    background: "rgba(34, 197, 94, 0.14)",
    border: "1px solid rgba(34, 197, 94, 0.45)",
    color: "#111827",
  };
}

export function explainScore(chunk) {
  if (chunk.riskScore === 0) return "Паттернов не найдено — балл нулевой.";

  const parts = [];
  const high = chunk.flags?.filter((f) => f.severity === "high") || [];
  const medium = chunk.flags?.filter((f) => f.severity === "medium") || [];
  const low = chunk.flags?.filter((f) => f.severity === "low") || [];

  if (high.length > 0) parts.push(`сильные паттерны: ${high.map((f) => f.type).join(", ")}`);
  if (medium.length > 0) parts.push(`умеренные паттерны: ${medium.map((f) => f.type).join(", ")}`);
  if (low.length > 0) parts.push(`слабые паттерны: ${low.map((f) => f.type).join(", ")}`);
  if (chunk.rhythmMonotony > 40) parts.push(`монотонный ритм (${chunk.rhythmMonotony}/100)`);
  if (chunk.lexicalPredictability > 40) parts.push(`предсказуемая лексика (${chunk.lexicalPredictability}/100)`);

  if (parts.length === 0) return "Незначительные отклонения по метрикам.";
  return `Из чего складывается балл: ${parts.join("; ")}.`;
}

export function renderHighlightedText(chunk) {
  const highlights = chunk.highlights || [];

  if (!highlights.length) return chunk.text;

  const sorted = [...highlights]
    .filter((h) => h.start >= 0 && h.end > h.start)
    .sort((a, b) => a.start - b.start);

  const parts = [];
  let cursor = 0;

  sorted.forEach((highlight, index) => {
    if (highlight.start < cursor) return;
    if (highlight.start > cursor) parts.push(chunk.text.slice(cursor, highlight.start));

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

  if (cursor < chunk.text.length) parts.push(chunk.text.slice(cursor));
  return parts;
}
