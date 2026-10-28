import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { formatDate } from '../../utils/formatDate';

const ScoreTrendChart = ({ data }) => {
  return (
    <div className="h-72 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          data={data}
          margin={{ top: 5, right: 20, left: -20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#e4e8e2" vertical={false} />
          <XAxis 
            dataKey="date" 
            tickFormatter={(tick) => formatDate(tick, 'MMM d')}
            stroke="#687169"
            tick={{ fill: '#687169', fontSize: 12 }}
            tickLine={false}
            axisLine={{ stroke: '#e4e8e2' }}
          />
          <YAxis 
            domain={[0, 100]} 
            stroke="#687169"
            tick={{ fill: '#687169', fontSize: 12 }}
            tickLine={false}
            axisLine={false}
          />
          <Tooltip 
            contentStyle={{ backgroundColor: '#ffffff', borderColor: '#e4e8e2', color: '#242a25', borderRadius: '8px', boxShadow: '0 4px 12px rgb(34 44 37 / 10%)' }}
            labelFormatter={(label) => formatDate(label)}
          />
          <Legend wrapperStyle={{ paddingTop: '10px' }} />
          <Line 
            type="monotone" 
            name="Overall Score"
            dataKey="score" 
            stroke="#52786e"
            strokeWidth={3}
            activeDot={{ r: 6, fill: '#52786e', stroke: '#ffffff', strokeWidth: 2 }}
            dot={{ r: 4, fill: '#ffffff', stroke: '#52786e', strokeWidth: 2 }}
          />
          <Line 
            type="monotone" 
            name="Coding Score"
            dataKey="codingScore" 
            stroke="#6b8f71"
            strokeWidth={2}
            dot={false}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default ScoreTrendChart;
