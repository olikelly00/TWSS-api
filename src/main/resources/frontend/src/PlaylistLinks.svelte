<script>
  import { createEventDispatcher } from 'svelte';
  const dispatch = createEventDispatcher();

  export let backend;
  let playlists = null;

  backend.subscribe("PLAYLIST_LIST", newPlaylists => {
    playlists = newPlaylists;
  });

  function showPlaylist(playlist) {
    dispatch('showPlaylist', playlist);
  }

  async function addPlaylist() {
    let playlist = { name: newPlaylistName };
    newPlaylistName = '';
    await backend.addPlaylist(playlist);
  }

  let newPlaylistName = '';
</script>

{#if playlists}
  {#each playlists as playlist}
    <div><button on:click={() => showPlaylist(playlist)}>{playlist.name}</button></div>
  {/each}
  <div class="new-playlist-form">
    <h4>Create new playlist</h4>
    <label for="new-playlist-name">Playlist Name</label>
    <div><input id="new-playlist-name" type="text" bind:value={newPlaylistName}></div>
    <div><button on:click={addPlaylist}>Add Playlist</button></div>
  </div>
{:else}
  <p>Fetching playlists...</p>
{/if}

<style>
  .new-playlist-form {
    margin: 1em 0;
  }
</style>
