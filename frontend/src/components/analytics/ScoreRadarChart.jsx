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
          <PolarGrid stroke="#e4e8e2" />
          <PolarAngleAxis dataKey="subject" tick={{ fill: '#687169', fontSize: 12 }} />
          <PolarRadiusAxis angle={30} domain={[0, 100]} tick={{ fill: '#687169' }} />
          <Tooltip 
            contentStyle={{ backgroundColor: '#ffffff', borderColor: '#e4e8e2', color: '#242a25' }}
            itemStyle={{ color: '#52786e' }}
          />
          <Radar
            name="Score"
            dataKey="score"
            stroke="#52786e"
            fill="#52786e"
            fillOpacity={0.5}
          />
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default ScoreRadarChart;
