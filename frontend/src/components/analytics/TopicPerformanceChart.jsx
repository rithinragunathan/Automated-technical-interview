import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';

const TopicPerformanceChart = ({ data }) => {
  const getBarColor = (score) => {
    if (score >= 80) return '#2ea043';
    if (score >= 60) return '#d29922';
    return '#f85149';
  };

  return (
    <div className="h-64 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          data={data}
          layout="vertical"
          margin={{ top: 5, right: 30, left: 40, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#30363d" horizontal={true} vertical={false} />
          <XAxis 
            type="number" 
            domain={[0, 100]} 
            stroke="#8b949e" 
            tick={{ fill: '#8b949e', fontSize: 12 }}
          />
          <YAxis 
            dataKey="topic" 
            type="category" 
            stroke="#8b949e" 
            tick={{ fill: '#8b949e', fontSize: 12 }}
            axisLine={false}
            tickLine={false}
          />
          <Tooltip
            cursor={{ fill: '#30363d', opacity: 0.4 }}
            contentStyle={{ backgroundColor: '#161b22', borderColor: '#30363d', color: '#e6edf3', borderRadius: '8px' }}
          />
          <Bar dataKey="score" radius={[0, 4, 4, 0]} barSize={20}>
            {data?.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={getBarColor(entry.score)} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default TopicPerformanceChart;
