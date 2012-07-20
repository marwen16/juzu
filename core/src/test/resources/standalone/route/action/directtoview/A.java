/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package standalone.route.action.directtoview;

import juzu.Action;
import juzu.Controller;
import juzu.Response;
import juzu.View;
import juzu.standalone.JuzuServlet;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class A extends Controller {

  @View
  public Response.Content index() {
    return Response.render(
        "<form id='form' action='" + A_.fooURL() + "' method='post'>" +
            "<input id='trigger' type='submit' name='click'/>" +
            "</form>");
  }

  @Action(route = "/foo")
  public Response.Update foo() {
    return A_.bar("juu").with(JuzuServlet.REDIRECT_AFTER_ACTION, false);
  }

  @View(route = "/bar")
  public Response.Content bar(String juu) {
    String path = renderContext.getProperty(JuzuServlet.PATH);
    return Response.ok("/juzu/foo".equals(path) && "juu".equals(juu) ? "pass" : "fail");
  }
}
