/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appengine.channel;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetTokenServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String gameId = req.getParameter("gamekey");
    Objectify ofy = ObjectifyService.ofy();
    Game game = ofy.load().type(Game.class).id(gameId).safe();

    String currentUserId = userService.getCurrentUser().getUserId();
    if (currentUserId.equals(game.getUserX()) || currentUserId.equals(game.getUserO())) {
      String channelKey = game.getChannelKey(currentUserId);
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      resp.setContentType("text/plain");
      resp.getWriter().println(channelService.createChannel(channelKey));
      return;
    }
    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
  }
}
