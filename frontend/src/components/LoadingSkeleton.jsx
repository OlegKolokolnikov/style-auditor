export default function LoadingSkeleton() {
  return (
    <div className="skeletonWrap">
      <div className="skeletonBox skeletonSummary" />
      <div className="skeletonLayout">
        <div className="skeletonSidebar">
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="skeletonChunkBtn" />
          ))}
        </div>
        <div className="skeletonMain">
          <div className="skeletonLine wide" />
          <div className="skeletonLine medium" />
          <div className="skeletonLine short" />
          <div className="skeletonBlock" />
        </div>
      </div>
    </div>
  );
}
