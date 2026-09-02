import React from 'react';

const ProgressBar = ({ value, max = 100, color = 'bg-brand-500', height = 'h-2', showLabel = false }) => {
  const safeValue = Math.min(max, Math.max(0, value || 0));
  const percentage = (safeValue / max) * 100;

  return (
    <div className="w-full">
      {showLabel && (
        <div className="flex justify-between text-xs mb-1">
          <span className="text-gray-400">Progress</span>
          <span className="text-white font-medium">{Math.round(percentage)}%</span>
        </div>
      )}
      <div className={`w-full bg-[#30363d] rounded-full overflow-hidden ${height}`}>
        <div
          className={`${height} ${color} transition-all duration-500 ease-out`}
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  );
};

export default ProgressBar;
