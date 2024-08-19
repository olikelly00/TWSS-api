<script>
  import { createEventDispatcher } from 'svelte';
  export let backend;

  const dispatch = createEventDispatcher();

  let files;
  let fileField;
  let title = '';
  let artist = '';
  let state = 'READY';

  async function submitTrack(event) {
    state = "SUBMITTING";

    let uploadRequestInfo = await backend.getUploadRequestInfo(
      files[0].name,
      files[0].type
    );

    await uploadTrackFile(uploadRequestInfo.signedUploadUrl, files[0]);

    await backend.submitTrack(title, artist, uploadRequestInfo.publicUrl);

    title = '';
    artist = '';
    fileField.value = '';
    state = "READY";
  }

  async function uploadTrackFile(url, file) {
    let trackUploadRes = await fetch(url, {
        method: 'PUT',
        body: file,
        headers: {
          'Content-Type': file.type
        }
      });

    if (trackUploadRes.status !== 200) {
      throw new Error("Error uploading track file: " + trackUploadRes.statusText);
    }
  }
</script>

<div class="track">
  <div class="track-name">
    <label for="new-track-title">Title</label>
    <input id="new-track-title" bind:value={title} bind:this={fileField} disabled={state == "SUBMITTING"}>
  </div>
  <div class="track-artist">
    <label for="new-track-artist">Artist</label>
    <input id="new-track-artist" bind:value={artist} disabled={state == "SUBMITTING"}>
  </div>
  <div class="track-player">
    <label for="new-track-file-upload">Upload</label>
    <input type="file" id="new-track-file-upload" bind:files disabled={state == "SUBMITTING"}>
  </div>
  <div class="track-ctrl">
    <button on:click={submitTrack} disabled={state == "SUBMITTING"}>Add</button>
  </div>
</div>

<style>
  .track {
    display: grid;
    grid-template-columns: 1fr 1fr 3fr 1fr;
    grid-gap: 10px;
    margin-bottom: 10px;
  }

  input {
    width: 100%;
  }

  .track-ctrl {
    vertical-align: middle;
    margin-top: 1.2em;
  }


</style>
