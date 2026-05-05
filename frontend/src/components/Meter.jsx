export default function Meter({ label, value, tooltip }) {
  const safeValue = Number.isFinite(Number(value)) ? Number(value) : 0;
  const width = Math.min(100, Math.max(0, safeValue));

  return (
    <div className="meter">
      <div className="meterTop">
        <span>
          {label}
          {tooltip && <abbr className="meterHint" title={tooltip}>?</abbr>}
        </span>
        <b>{safeValue}/100</b>
      </div>
      <div className="meterBar">
        <div style={{ width: `${width}%` }} />
      </div>
    </div>
  );
}
