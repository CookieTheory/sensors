import './App.css'
import React, { useEffect, useState } from 'react';
import { Flex, Progress, Space } from 'antd';

function getStrokeColor(temp) {
    if (temp < 50) return '#00FF00';
    if (temp < 70) return '#FFA500';
    return '#FF0000';
}

function App() {
    const [temps, setTemps] = useState({});
    const [watts, setWatts] = useState({});
    const [networkSpeed, setNetworkSpeed] = useState({});

    useEffect(() => {
        const fetchTemps = () => {
            fetch('/sensors/api/tempsjson')
                .then(res => res.json())
                .then(data => setTemps(data))
                .catch(err => console.error('Error fetching temps:', err));
        };

        const fetchWatts = () => {
            fetch('/sensors/api/wattsjson')
                .then(res => res.json())
                .then(data => setWatts(data))
                .catch(err => console.error('Error fetching watts:', err));
        };

        const fetchNetworkSpeed = () => {
                fetch('/sensors/api/networkjson')
                    .then(res => res.json())
                    .then(data => setNetworkSpeed(data))
                    .catch(err => console.error('Error fetching watts:', err));
            };

        const fetchAll = () => {
            fetchTemps();
            fetchWatts();
            fetchNetworkSpeed();
        };

        fetchAll(); // initial fetch
        const interval = setInterval(fetchAll, 1000); // every second

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
    const networkEntries = Object.entries(networkSpeed);

    return (
        <>
            <div style={{ display: 'flex', justifyContent: 'center', gap: '24px' }}>
                <div>
                    <div style={{ display: 'flex', justifyContent: 'center', gap: '24px' }}>
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
                                    percent={(Math.round(wattEntry[1])/75)*100}
                                    format={() => `${Number(wattEntry[1]).toFixed(2)} W`}
                                    strokeColor={getStrokeColor(wattEntry[1])}
                                    trailColor="#FFFFFF"
                                />
                            </div>
                        )}
                    </div>
                    <div style={{ color: 'white', marginBottom: '40px' }}>{"Package"}</div>
                </div>
                <div style={{ display: 'flex', justifyContent: 'center', gap: '24px' }}>
                    {networkEntries.map(([label, speed], index) => (
                        <div key={index} style={{ textAlign: 'center' }}>
                            <Progress style={{ fontSize: '12px' }}
                                type="dashboard"
                                percent={(Math.round(speed))/1000}
                                format={() => `${Math.round(speed)} Kbit/s`}
                                strokeColor={getStrokeColor(speed/1000)}
                                trailColor="#FFFFFF"
                            />
                            <div style={{ color: 'white', marginBottom: '40px' }}>{label}</div>
                        </div>
                    ))}
                </div>
            </div>

            <Space wrap size="large" style={{ justifyContent: 'center', width: '100%', gap: '24px' }}>
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
