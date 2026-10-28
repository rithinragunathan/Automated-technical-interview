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
          <CartesianGrid strokeDasharray="3 3" stroke="#e4e8e2" vertical={false} />
          <XAxis 
            dataKey="difficulty" 
            stroke="#687169"
            tick={{ fill: '#687169', fontSize: 12 }}
            axisLine={{ stroke: '#e4e8e2' }}
            tickLine={false}
          />
          <YAxis 
            stroke="#687169"
            tick={{ fill: '#687169', fontSize: 12 }}
            axisLine={false}
            tickLine={false}
          />
          <Tooltip
            cursor={{ fill: '#e4e8e2', opacity: 0.4 }}
            contentStyle={{ backgroundColor: '#ffffff', borderColor: '#e4e8e2', color: '#242a25', borderRadius: '8px' }}
          />
          <Legend wrapperStyle={{ paddingTop: '10px' }} />
          <Bar dataKey="score" name="Success Rate" fill="#52786e" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default DifficultyPerformanceChart;
