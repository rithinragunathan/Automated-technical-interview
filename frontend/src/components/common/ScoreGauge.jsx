import React from 'react';

const ScoreGauge = ({ score, size = 120, strokeWidth = 10, label = 'Score' }) => {
  const radius = (size - strokeWidth) / 2;
  const circumference = radius * 2 * Math.PI;
  const safeScore = Math.min(100, Math.max(0, score || 0));
  const strokeDashoffset = circumference - (safeScore / 100) * circumference;
  
  let color = '#f85149'; // Red
  if (safeScore >= 80) color = '#2ea043'; // Green
  else if (safeScore >= 60) color = '#d29922'; // Yellow/Orange

  return (
    <div className="relative flex flex-col items-center justify-center" style={{ width: size, height: size }}>
      <svg className="transform -rotate-90 w-full h-full">
        {/* Background Circle */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          stroke="#30363d"
          strokeWidth={strokeWidth}
          fill="transparent"
        />
        {/* Progress Circle */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          stroke={color}
          strokeWidth={strokeWidth}
          fill="transparent"
          strokeDasharray={circumference}
          strokeDashoffset={strokeDashoffset}
          className="transition-all duration-1000 ease-out"
          strokeLinecap="round"
        />
      </svg>
      <div className="absolute flex flex-col items-center justify-center text-center">
        <span className="text-2xl font-bold text-white">{safeScore}</span>
        {label && <span className="text-xs text-gray-400 mt-1">{label}</span>}
      </div>
    </div>
  );
};

export default ScoreGauge;
