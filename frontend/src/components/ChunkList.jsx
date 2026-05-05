import { riskClass } from "../utils/helpers";

export default function ChunkList({ chunks, activeChunk, onSelect }) {
  return (
    <aside className="chunkList">
      <h2>Чанки</h2>
      {chunks.map((chunk) => (
        <button
          key={chunk.index}
          className={`chunkButton ${riskClass(chunk.riskScore)} ${activeChunk === chunk.index ? "active" : ""}`}
          onClick={() => onSelect(activeChunk === chunk.index ? null : chunk.index)}
        >
          <span>#{chunk.index + 1}</span>
          <b>индекс {chunk.riskScore}/100</b>
          <small>{chunk.label}</small>
        </button>
      ))}
    </aside>
  );
}
