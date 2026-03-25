import { Routes, Route } from 'react-router-dom';
import './App.css';
import SiteListPage from './pages/SiteListPage';
import SiteDetailPage from './pages/SiteDetailPage';
import EntryDetailPage from './pages/EntryDetailPage';
import EntryCreatePage from './pages/EntryCreatePage';
import LiquidEther from './components/LiquidEther';

function App() {
  return (
    <>
      <LiquidEther
        className="void-background"
        colors={['#840dd3', '#9a08dd', '#0fdb13']}
        autoDemo={true}
        autoSpeed={0.5}
        autoIntensity={2.2}
        mouseForce={20}
        resolution={0.5}
      />
      <div className="app">
        <header className="app-header">
          <a href="/" className="header-link">
            <img src="/void-logo.jpg" alt="The Void" className="header-logo" />
            <h1>THE VOID</h1>
            <p className="subtitle">Static Site Generator with Cosmic Horror</p>
          </a>
        </header>

        <main className="app-main">
          <Routes>
            <Route path="/" element={<SiteListPage />} />
            <Route path="/site/:siteId" element={<SiteDetailPage />} />
            <Route path="/site/:siteId/entries/new" element={<EntryCreatePage />} />
            <Route path="/site/:siteId/entries/:slug" element={<EntryDetailPage />} />
          </Routes>
        </main>
      </div>
    </>
  );
}

export default App;