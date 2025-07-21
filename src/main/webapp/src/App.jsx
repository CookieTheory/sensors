import './App.css'
import React, { useEffect, useState } from 'react';
import { Flex, Progress, Space } from 'antd';

function getStrokeColor(temp) {
  if (temp < 50) return '#00FF00';    // green
  if (temp < 70) return '#FFA500';    // orange
  return '#FF0000';                   // red
}

function App() {
    const [temps, setTemps] = useState({});
    const [watts, setWatts] = useState({});

    useEffect(() => {
        const fetchTemps = () => {
            fetch('/sensors/api/tempsjson')
                .then(res => res.json())
                .then(data => setTemps(data))
                .catch(err => console.error('Error fetching temps:', err));
        };

        fetchTemps(); // initial fetch
        const interval = setInterval(fetchTemps, 1000); // every second

        return () => clearInterval(interval); // cleanup on unmount
    }, []);

    useEffect(() => {
        const fetchWatts = () => {
            fetch('/sensors/api/wattsjson')
                .then(res => res.json())
                .then(data => setWatts(data))
                .catch(err => console.error('Error fetching watts:', err));
        };

        fetchWatts(); // initial fetch
        const interval = setInterval(fetchWatts, 1000); // every second

        return () => clearInterval(interval); // cleanup on unmount
    }, []);

    // Separate package and cores
    const entries = Object.entries(temps);
    const packageEntry = entries.find(([key]) => key.toLowerCase().includes('package'));
    const coreEntries = entries.filter(([key]) => !key.toLowerCase().includes('package'));
    const sortedEntries = coreEntries.sort(([keyA], [keyB]) => {
        const getCoreNumber = (key) => {
            const match = key.match(/\d+/);
            return match ? parseInt(match[0], 10) : Infinity;
        };
        return getCoreNumber(keyA) - getCoreNumber(keyB);
    });
    const wattEntry = Object.entries(watts).find(([key]) => key.toLowerCase().includes('watts'));

    return (
        <>
            <div style={{ display: 'flex', justifyContent: 'center', gap: '40px' }}>
                {packageEntry && (
                    <div style={{ textAlign: 'center', marginBottom: '4px' }}>
                        <Progress
                            type="dashboard"
                            percent={Math.round(packageEntry[1])}
                            format={() => `${Math.round(packageEntry[1])}°C`}
                            strokeColor={getStrokeColor(packageEntry[1])}
                            trailColor="#FFFFFF"
                        />
                    </div>
                )}
                {wattEntry && (
                    <div style={{ textAlign: 'center', marginBottom: '4px' }}>
                        <Progress
                            type="dashboard"
                            percent={Math.round(wattEntry[1])}
                            format={() => `${Number(wattEntry[1]).toFixed(2)} W`}
                            strokeColor={getStrokeColor(wattEntry[1])}
                            trailColor="#FFFFFF"
                        />
                    </div>
                )}
            </div>
            <div style={{ color: 'white', marginBottom: '40px' }}>{"Package"}</div>

            <Space wrap size="large" style={{ justifyContent: 'center', width: '100%' }}>
                {sortedEntries.map(([label, temp], index) => (
                    <div key={index} style={{ textAlign: 'center' }}>
                        <Progress
                            type="dashboard"
                            percent={Math.round(temp)}
                            format={() => `${Math.round(temp)}°C`}
                            strokeColor={getStrokeColor(temp)}
                            trailColor="#FFFFFF"
                        />
                        <div style={{ color: 'white', marginTop: '0px' }}>{label}</div>
                    </div>
                ))}
            </Space>
            </>
    )
}

export default App
