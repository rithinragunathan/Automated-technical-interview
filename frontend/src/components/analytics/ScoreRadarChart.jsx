import React from 'react';
import { Radar, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer, Tooltip } from 'recharts';

const ScoreRadarChart = ({ data }) => {
  // data format expected: [{ subject: 'Math', A: 120, fullMark: 150 }, ...]
  
  const formattedData = data || [
    { subject: 'Coding', score: 85, fullMark: 100 },
    { subject: 'Algorithms', score: 70, fullMark: 100 },
    { subject: 'System Design', score: 60, fullMark: 100 },
    { subject: 'Communication', score: 90, fullMark: 100 },
    { subject: 'Problem Solving', score: 80, fullMark: 100 },
  ];

  return (
    <div className="h-64 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <RadarChart cx="50%" cy="50%" outerRadius="70%" data={formattedData}>
          <PolarGrid stroke="#30363d" />
          <PolarAngleAxis dataKey="subject" tick={{ fill: '#8b949e', fontSize: 12 }} />
          <PolarRadiusAxis angle={30} domain={[0, 100]} tick={{ fill: '#8b949e' }} />
          <Tooltip 
            contentStyle={{ backgroundColor: '#161b22', borderColor: '#30363d', color: '#e6edf3' }}
            itemStyle={{ color: '#4c6ef5' }}
          />
          <Radar
            name="Score"
            dataKey="score"
            stroke="#4c6ef5"
            fill="#4c6ef5"
            fillOpacity={0.5}
          />
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default ScoreRadarChart;
