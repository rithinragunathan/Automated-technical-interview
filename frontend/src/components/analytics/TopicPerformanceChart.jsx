import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';

const TopicPerformanceChart = ({ data }) => {
  const getBarColor = (score) => {
    if (score >= 80) return '#52786e';
    if (score >= 60) return '#aa812d';
    return '#ad5144';
  };

  return (
    <div className="h-64 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          data={data}
          layout="vertical"
          margin={{ top: 5, right: 30, left: 40, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#e4e8e2" horizontal={true} vertical={false} />
          <XAxis 
            type="number" 
            domain={[0, 100]} 
            stroke="#687169"
            tick={{ fill: '#687169', fontSize: 12 }}
          />
          <YAxis 
            dataKey="topic" 
            type="category" 
            stroke="#687169"
            tick={{ fill: '#687169', fontSize: 12 }}
            axisLine={false}
            tickLine={false}
          />
          <Tooltip
            cursor={{ fill: '#e4e8e2', opacity: 0.4 }}
            contentStyle={{ backgroundColor: '#ffffff', borderColor: '#e4e8e2', color: '#242a25', borderRadius: '8px' }}
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
