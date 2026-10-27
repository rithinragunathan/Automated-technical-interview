import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const DifficultyPerformanceChart = ({ data }) => {
  return (
    <div className="h-64 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          data={data}
          margin={{ top: 20, right: 30, left: -20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#30363d" vertical={false} />
          <XAxis 
            dataKey="difficulty" 
            stroke="#8b949e" 
            tick={{ fill: '#8b949e', fontSize: 12 }}
            axisLine={{ stroke: '#30363d' }}
            tickLine={false}
          />
          <YAxis 
            stroke="#8b949e" 
            tick={{ fill: '#8b949e', fontSize: 12 }}
            axisLine={false}
            tickLine={false}
          />
          <Tooltip
            cursor={{ fill: '#30363d', opacity: 0.4 }}
            contentStyle={{ backgroundColor: '#161b22', borderColor: '#30363d', color: '#e6edf3', borderRadius: '8px' }}
          />
          <Legend wrapperStyle={{ paddingTop: '10px' }} />
          <Bar dataKey="score" name="Success Rate" fill="#4c6ef5" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default DifficultyPerformanceChart;
