export default function SummaryBox({ result, isStale }) {
  return (
    <section className={`summaryBox${isStale ? " stale" : ""}`}>
      {isStale && (
        <p className="staleNotice">Текст изменён — результаты могут быть устаревшими.</p>
      )}
      <h2>{result.smoothnessLabel}</h2>
      <p className="summaryVerdict">{result.verdict}</p>
      <p className="summaryNote">Индекс отражает сглаженность текста, а не «силу ошибки».</p>
    </section>
  );
}
