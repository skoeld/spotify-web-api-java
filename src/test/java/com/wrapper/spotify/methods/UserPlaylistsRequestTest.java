package com.wrapper.spotify.methods;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.TestUtil;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.SpotifyEntityType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.*;

public class UserPlaylistsRequestTest {

  @Test
  public void shouldGetPlaylistsForUser_async() throws Exception {
    final String accessToken = "someAccessToken";
    final Api api = Api.builder().build();

    final UserPlaylistsRequest request = api
            .getPlaylistsForUser("wizzler")
            .accessToken(accessToken)
            .limit(10)
            .offset(2)
            .httpManager(TestUtil.MockedHttpManager.returningJson("user-playlists.json"))
            .build();

    final CountDownLatch asyncCompleted = new CountDownLatch(1);

    final SettableFuture<Page<SimplePlaylist>> playlistsPageFuture = request.getAsync();

    Futures.addCallback(playlistsPageFuture, new FutureCallback<Page<SimplePlaylist>>() {
      @Override
      public void onSuccess(Page<SimplePlaylist> playlistsPage) {
        assertTrue(playlistsPage.getTotal() >= 0);
        assertNull(playlistsPage.getNext());
        assertNull(playlistsPage.getPrevious());
        assertEquals(10, playlistsPage.getLimit());
        assertEquals(2, playlistsPage.getOffset());
        assertEquals("https://api.spotify.com/v1/users/wizzler/playlists", playlistsPage.getHref());

        final SimplePlaylist simplePlaylist = playlistsPage.getItems().get(0);
        final String playlistId = simplePlaylist.getId();
        assertNotNull(playlistId);
        assertTrue(playlistId.length() > 0);
        assertEquals(false, simplePlaylist.isCollaborative());
        assertEquals("http://open.spotify.com/user/wizzler/playlists/" + playlistId, simplePlaylist.getExternalUrls().get("spotify"));
        assertNotNull(simplePlaylist.getName());
        assertNotNull(simplePlaylist.getOwner());
        assertNotNull(simplePlaylist.isPublicAccess());
        assertNotNull(simplePlaylist.getTracks().getHref());
        assertNotNull(simplePlaylist.getTracks().getTotal());
        assertEquals(SpotifyEntityType.PLAYLIST, simplePlaylist.getType());
        assertEquals("spotify:user:wizzler:playlist:" + playlistId, simplePlaylist.getUri());

        asyncCompleted.countDown();
      }

      @Override
      public void onFailure(Throwable throwable) {
        fail("Failed to resolve future" + throwable.getMessage());
      }

    });

    asyncCompleted.await(1, TimeUnit.SECONDS);
  }

  @Test
  public void shouldGetPlaylistsForUser_sync() throws Exception {
    final String accessToken = "myVeryLongAccessToken";
    final Api api = Api.builder().build();

    final UserPlaylistsRequest request = api
            .getPlaylistsForUser("wizzler")
            .accessToken(accessToken)
            .httpManager(TestUtil.MockedHttpManager.returningJson("user-playlists.json"))
            .build();

    final Page<SimplePlaylist> playlistsPage = request.get();

    assertTrue(playlistsPage.getTotal() >= 0);
    assertNull(playlistsPage.getNext());
    assertNull(playlistsPage.getPrevious());
    assertEquals(10, playlistsPage.getLimit());
    assertEquals(2, playlistsPage.getOffset());
    assertEquals("https://api.spotify.com/v1/users/wizzler/playlists", playlistsPage.getHref());

    final SimplePlaylist simplePlaylist = playlistsPage.getItems().get(0);
    final String playlistId = simplePlaylist.getId();
    assertNotNull(playlistId);
    assertTrue(playlistId.length() > 0);
    assertEquals(false, simplePlaylist.isCollaborative());
    assertEquals("http://open.spotify.com/user/wizzler/playlists/" + playlistId, simplePlaylist.getExternalUrls().get("spotify"));
    assertNotNull(simplePlaylist.getName());
    assertNotNull(simplePlaylist.getOwner());
    assertNotNull(simplePlaylist.isPublicAccess());
    assertNotNull(simplePlaylist.getTracks().getHref());
    assertNotNull(simplePlaylist.getTracks().getTotal());
    assertEquals(SpotifyEntityType.PLAYLIST, simplePlaylist.getType());
    assertEquals("spotify:user:wizzler:playlist:" + playlistId, simplePlaylist.getUri());
  }

}
