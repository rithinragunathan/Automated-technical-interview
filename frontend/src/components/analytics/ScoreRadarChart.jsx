import React from 'react';
import { Radar, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer, Tooltip } from 'recharts';

const ScoreRadarChart = ({ data }) => {
  if (!data?.length) {
    return (
      <div className="h-64 w-full flex items-center justify-center text-sm text-gray-500">
        Complete an interview to see skill dimensions.
      </div>
    );
  }

  return (
    <div className="h-64 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <RadarChart cx="50%" cy="50%" outerRadius="70%" data={data}>
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
