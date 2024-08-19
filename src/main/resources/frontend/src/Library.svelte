<script>
  import NewTrack from './NewTrack.svelte';
  import TrackList from './TrackList.svelte';

  export let backend;
  let tracks = null;

  backend.subscribe("TRACK_LIST", newTrackList => {
    tracks = newTrackList;
  });

  function deleteTrack(evt) {
    let track = evt.detail;
    backend.deleteTrack(track.id);
  }
</script>

{#if tracks}
  <NewTrack backend={backend} />
  <TrackList tracks={tracks} on:deleteTrack={deleteTrack} />
{:else}
  <p>Loading...</p>
{/if}
