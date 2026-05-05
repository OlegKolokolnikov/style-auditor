import Meter from "./Meter";
import { riskClass, flagStyle, explainScore, renderHighlightedText } from "../utils/helpers";

export default function ChunkDetail({ chunk }) {
  return (
    <article className="chunk">
      <header className="chunkHeader">
        <div>
          <h3>Чанк #{chunk.index + 1}</h3>
          <p className="meta">
            {chunk.chars} симв. · {chunk.sentenceCount} предл. ·
            средняя длина {chunk.avgSentenceLength} · разброс {chunk.sentenceLengthStd}
          </p>
        </div>
        <div className={`riskBadge ${riskClass(chunk.riskScore)}`}>
          <small>индекс чанка</small>
          <br />
          <b>{chunk.riskScore}/100</b>
        </div>
      </header>

      <p className="chunkExplanation">{explainScore(chunk)}</p>

      <div className="meters">
        <Meter
          label="Ожидаемость лексики"
          value={chunk.lexicalPredictability}
          tooltip="Доля частотных, абстрактных и взаимозаменяемых слов. ИИ тяготеет к «страху», «тревоге», «вдруг» вместо конкретных деталей. Чем выше балл — тем более шаблонная лексика."
        />
        <Meter
          label="Монотонность ритма"
          value={chunk.rhythmMonotony}
          tooltip="Насколько ровны предложения по длине. ИИ генерирует предложения одинакового размера; живой автор неосознанно чередует короткие и длинные. Чем выше балл — тем ровнее ритм."
        />
      </div>

      {chunk.flags?.length > 0 ? (
        <div className="flags">
          {chunk.flags.map((flag, i) => (
            <div key={i} className={`flag ${flag.severity || "medium"}`} style={flagStyle(flag.severity)}>
              <div>
                <b>{flag.type}</b>
                <span>{flag.count} найдено</span>
              </div>
              <p>{flag.comment}</p>
            </div>
          ))}
        </div>
      ) : (
        <div className="clean">Сильных формальных флагов нет.</div>
      )}

      {chunk.suggestions?.length > 0 && (
        <div className="suggestions">
          <h3>Что можно проверить</h3>
          <ul>
            {chunk.suggestions.map((s, i) => <li key={i}>{s}</li>)}
          </ul>
        </div>
      )}

      <div className="textBlock">{renderHighlightedText(chunk)}</div>
    </article>
  );
}
