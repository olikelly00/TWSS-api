<script>
  import TrackList from './TrackList.svelte';
  import AddTrackForm from './AddTrackForm.svelte';

  export let playlistId;
  export let backend;
  let allTracks = null;
  let playlists = null;

  backend.subscribe("TRACK_LIST", newTracks => {
    allTracks = newTracks;
  })

  backend.subscribe("PLAYLIST_LIST", newPlaylists => {
    playlists = newPlaylists;
  })

  function deleteTrack(evt) {
    let track = evt.detail;
    backend.removeTrackFromPlaylist(playlistId, track.id);
  }

  async function addTrack(evt) {
    let track = evt.detail;
    backend.addTrackToPlaylist(playlistId, track.id);
  }

  function getPlaylist(playlists, playlistId) {
    return playlists.find(p => p.id == playlistId);
  }
</script>

{#if playlists}
  <h2>{getPlaylist(playlists, playlistId).name}</h2>
  <TrackList on:deleteTrack={deleteTrack} tracks={getPlaylist(playlists, playlistId).tracks} />
{/if}

{#if allTracks}
  <AddTrackForm on:addTrack={addTrack} allTracks={allTracks} />
{/if}

<style>
  .grid {
    display: grid;
    grid-template-columns: 1fr 3fr;
    grid-template-rows: auto;
    grid-template-areas: "sidebar main";
    grid-gap: 1rem;
  }
  .sidebar {
    grid-area: sidebar;
  }
  .main {
    grid-area: main;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }
</style>
